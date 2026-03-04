"""
BN1 hyperparameter grid search.

Runs HillClimbSearch + HCS restarts for every combination in GRID,
records BIC-d score and HCS metadata.  An ExperimentTracker handles
all directory layout, naming, and incremental saves — a crash only
loses the config currently in progress.

Usage
-----
    uv run python -m diff_analysis.scripts.megavul.bn1.tune_bn1

Outputs (under data/results/tune_bn1/<timestamp>/)
---------------------------------------------------
    experiment.json              full experiment config, written upfront
    configs/<NNN_slug>/
        config.json              hyperparams + started_at
        result.json              outcome + ended_at
        hcs_restarts.jsonl       one line per HCS restart
    summary.csv                  all configs, appended after each run
    summary.jsonl                same, JSONL format
"""

import itertools
import logging
import time
from datetime import datetime

import pandas as pd
from pgmpy.base import DAG as PgmDAG
from pgmpy.estimators import BIC as BicScore

from diff_analysis.scripts.megavul.bn_utils import BNPipeline
from diff_analysis.scripts.megavul.experiment_tracker import ExperimentTracker
from diff_analysis.utils.config_utils import find_project_root
from diff_analysis.utils.logging import setup_logging

setup_logging(level=logging.INFO)
logging.getLogger("pgmpy").setLevel(logging.WARNING)  # suppress per-restart datatype inference logs
logger = logging.getLogger(__name__)

TARGET_COL = "is_vul"

# ---------------------------------------------------------------------------
# Grid
# ---------------------------------------------------------------------------

GRID = {
    "mi_threshold": [100, 150, 200],
    "tabu_length":  [10, 50, 100],
    "max_indegree": [None, 3, 5],
}

# HCS params — kept fixed during grid search (looser c for speed)
HCS_DELTA         = 0.05
HCS_C             = 0.10   # looser than production (0.05) to keep each run bounded
HCS_MAX_RESTARTS  = 50
SCORING           = "bic-d"
MAX_ITER          = 1_000_000
FEATURE_THRESHOLD = 0.0001
SAMPLE_THRESHOLD  = 0.0


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def score_dag(dag, model_df: pd.DataFrame) -> float:
    scorer = BicScore(model_df)
    return sum(
        scorer.local_score(n, list(dag.predecessors(n))) for n in dag.nodes()
    )


# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------

if __name__ == "__main__":
    project_root = find_project_root()
    data_path    = project_root / "data" / "processed" / "feature_matrix.parquet"

    keys    = list(GRID.keys())
    configs = [dict(zip(keys, combo)) for combo in itertools.product(*GRID.values())]

    experiment_config = {
        "script":   "diff_analysis.scripts.megavul.bn1.tune_bn1",
        "grid":     GRID,
        "hcs_params": {
            "delta": HCS_DELTA, "c": HCS_C, "max_restarts": HCS_MAX_RESTARTS,
        },
        "fixed_params": {
            "scoring": SCORING, "max_iter": MAX_ITER,
            "feature_threshold": FEATURE_THRESHOLD,
            "sample_threshold":  SAMPLE_THRESHOLD,
            "target_col":        TARGET_COL,
        },
        "n_configs":               len(configs),
        "max_restarts_per_config": HCS_MAX_RESTARTS,
        "max_total_restarts":      len(configs) * HCS_MAX_RESTARTS,
    }

    tracker = ExperimentTracker(
        base_dir=project_root / "data" / "results" / "tune_bn1",
        experiment_config=experiment_config,
    )

    logger.info(f"Loading feature matrix from {data_path}")
    df_raw = pd.read_parquet(data_path)
    logger.info(f"Grid search: {len(configs)} configs × HCS (max {HCS_MAX_RESTARTS} restarts each)")
    t_experiment = time.time()

    for i, cfg in enumerate(configs, 1):
        mi    = cfg["mi_threshold"]
        tabu  = cfg["tabu_length"]
        indeg = cfg["max_indegree"]

        # Prominent notice before the first mi=200 config
        if mi == 200 and (i == 1 or configs[i - 2]["mi_threshold"] != 200):
            logger.warning("─" * 60)
            logger.warning(
                f"STARTING mi_threshold=200 configs [{i}–{len(configs)}]"
                " — expect significantly slower runs"
            )
            logger.warning("─" * 60)

        logger.info(f"[{i}/{len(configs)}] mi={mi}, tabu={tabu}, indeg={indeg}")

        cfg_dir    = tracker.config_dir(i, mi, tabu, indeg)
        started_at = datetime.now().isoformat(timespec="seconds")
        tracker.save_config_start(cfg_dir, {
            "config_index": i,
            "mi_threshold": mi, "tabu_length": tabu, "max_indegree": indeg,
            "started_at":   started_at,
        })

        t0 = time.time()
        try:
            pipeline = BNPipeline(df_raw, TARGET_COL)
            pipeline.preprocess(
                feature_threshold=FEATURE_THRESHOLD,
                sample_threshold=SAMPLE_THRESHOLD,
                mi_threshold=mi,
            ).learn_structure(
                method="hillclimb",
                scoring_method=SCORING,
                tabu_length=tabu,
                max_indegree=indeg,
                max_iter=MAX_ITER,
                hcs_delta=HCS_DELTA,
                hcs_c=HCS_C,
                hcs_max_restarts=HCS_MAX_RESTARTS,
                show_progress=True,
            )
        except Exception as e:
            logger.error(f"  FAILED: {e}")
            total_s = time.time() - t_experiment
            result = {
                "config_index": i,
                "mi_threshold": mi, "tabu_length": tabu, "max_indegree": indeg,
                "started_at": started_at,
                "ended_at":   datetime.now().isoformat(timespec="seconds"),
                "n_restarts_used": None, "n_distinct_optima": None,
                "n_edges": None, "bic_score": None,
                "n_edges_on_target": None,
                "elapsed_s":         round(time.time() - t0, 1),
                "total_elapsed_min": round(total_s / 60, 2),
                "total_elapsed_h":   round(total_s / 3600, 4),
                "error": str(e),
            }
            tracker.save_config_result(cfg_dir, result)
            logger.error(
                f"  config elapsed={result['elapsed_s']}s "
                f"| total {total_s/60:.1f}min ({total_s/3600:.2f}h) [{i}/{len(configs)}]"
            )
            continue

        if pipeline.hcs_history:
            tracker.save_hcs_restarts(cfg_dir, pipeline.hcs_history)

        best_dag = PgmDAG()
        best_dag.add_nodes_from(pipeline.model_df.columns)
        best_dag.add_edges_from(pipeline.edges)
        bic = score_dag(best_dag, pipeline.model_df)

        n_on_target = sum(1 for u, v in pipeline.edges if TARGET_COL in (u, v))
        n_distinct  = len({
            frozenset(tuple(e) for e in r["edges"])
            for r in pipeline.hcs_history
        }) if pipeline.hcs_history else None

        total_s = time.time() - t_experiment
        result = {
            "config_index":        i,
            "mi_threshold":        mi,
            "tabu_length":         tabu,
            "max_indegree":        indeg,
            "started_at":          started_at,
            "ended_at":            datetime.now().isoformat(timespec="seconds"),
            "n_restarts_used":     pipeline.hcs_n_restarts,
            "n_distinct_optima":   n_distinct,
            "n_edges":             len(pipeline.edges),
            "bic_score":           round(bic, 2),
            "n_edges_on_target":   n_on_target,
            "elapsed_s":           round(time.time() - t0, 1),
            "total_elapsed_min":   round(total_s / 60, 2),
            "total_elapsed_h":     round(total_s / 3600, 4),
            "error":               None,
        }
        tracker.save_config_result(cfg_dir, result)
        logger.info(
            f"  → bic={bic:.1f}, edges={len(pipeline.edges)}, "
            f"on_target={n_on_target}, restarts={pipeline.hcs_n_restarts}, "
            f"distinct={n_distinct}, config elapsed={result['elapsed_s']}s "
            f"| total {total_s/60:.1f}min ({total_s/3600:.2f}h) [{i}/{len(configs)}]"
        )

    tracker.finalize()
