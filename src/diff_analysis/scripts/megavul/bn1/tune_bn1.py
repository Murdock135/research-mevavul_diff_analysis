"""
BN1 hyperparameter grid search — parallel config execution.

Runs HillClimbSearch + HCS restarts for every combination in GRID using
multiprocessing.Pool (N_WORKERS parallel configs).  Each worker writes only
to its own isolated config directory; ExperimentTracker.finalize() collects
all result.json files and builds summary.csv / summary.jsonl.

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
    summary.csv                  written by finalize() sorted by bic_score desc
    summary.jsonl                same, JSONL format
"""

import itertools
import json
import logging
import time
from datetime import datetime
from multiprocessing import Pool

import pandas as pd
import tqdm
from pgmpy.base import DAG as PgmDAG
from pgmpy.estimators import BIC as BicScore

from diff_analysis.scripts.megavul.bn_utils import BNPipeline
from diff_analysis.scripts.megavul.experiment_tracker import ExperimentTracker
from diff_analysis.utils.config_utils import find_project_root
from diff_analysis.utils.logging import setup_logging

setup_logging(level=logging.INFO)
logging.getLogger("pgmpy").setLevel(logging.WARNING)
logger = logging.getLogger(__name__)

TARGET_COL = "is_vul"
N_WORKERS  = 6

# ---------------------------------------------------------------------------
# Grid
# ---------------------------------------------------------------------------

GRID = {
    "mi_threshold": [150, 200],
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
# Worker function (module-level so Pool can pickle it)
# ---------------------------------------------------------------------------

def _score_dag(dag, model_df: pd.DataFrame) -> float:
    scorer = BicScore(model_df)
    return sum(
        scorer.local_score(n, list(dag.predecessors(n))) for n in dag.nodes()
    )


def run_config(args: tuple) -> dict:
    """Run one grid config; write config.json, result.json, hcs_restarts.jsonl."""
    i, cfg, df_raw, cfg_dir, show_progress = args

    # Worker processes need their own logging setup
    setup_logging(level=logging.INFO)
    logging.getLogger("pgmpy").setLevel(logging.WARNING)
    wlog = logging.getLogger(__name__)

    mi    = cfg["mi_threshold"]
    tabu  = cfg["tabu_length"]
    indeg = cfg["max_indegree"]

    started_at = datetime.now().isoformat(timespec="seconds")
    with open(cfg_dir / "config.json", "w") as f:
        json.dump({
            "config_index": i,
            "mi_threshold": mi, "tabu_length": tabu, "max_indegree": indeg,
            "started_at":   started_at,
        }, f, indent=2, default=str)

    wlog.info(f"[cfg {i}] START mi={mi}, tabu={tabu}, indeg={indeg}")
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
            show_progress=show_progress,
            hcs_restarts_file=cfg_dir / "hcs_restarts.jsonl",
        )
    except Exception as e:
        wlog.error(f"[cfg {i}] FAILED: {e}")
        elapsed = round(time.time() - t0, 1)
        result = {
            "config_index":    i,
            "mi_threshold":    mi, "tabu_length": tabu, "max_indegree": indeg,
            "started_at":      started_at,
            "ended_at":        datetime.now().isoformat(timespec="seconds"),
            "n_restarts_used": None, "n_distinct_optima": None,
            "n_edges":         None, "bic_score": None,
            "n_edges_on_target": None,
            "elapsed_s":       elapsed,
            "error":           str(e),
        }
        with open(cfg_dir / "result.json", "w") as f:
            json.dump(result, f, indent=2, default=str)
        return result

    # Narrow Optional types after successful pipeline run
    model_df = pipeline.model_df
    edges    = pipeline.edges
    assert model_df is not None and edges is not None

    best_dag = PgmDAG()
    best_dag.add_nodes_from(model_df.columns)
    best_dag.add_edges_from(edges)
    bic = _score_dag(best_dag, model_df)

    n_on_target = sum(1 for u, v in edges if TARGET_COL in (u, v))
    n_distinct  = len({
        frozenset(tuple(e) for e in r["edges"])
        for r in pipeline.hcs_history
    }) if pipeline.hcs_history else None

    elapsed = round(time.time() - t0, 1)
    result = {
        "config_index":      i,
        "mi_threshold":      mi,
        "tabu_length":       tabu,
        "max_indegree":      indeg,
        "started_at":        started_at,
        "ended_at":          datetime.now().isoformat(timespec="seconds"),
        "n_restarts_used":   pipeline.hcs_n_restarts,
        "n_distinct_optima": n_distinct,
        "n_edges":           len(edges),
        "bic_score":         round(bic, 2),
        "n_edges_on_target": n_on_target,
        "elapsed_s":         elapsed,
        "error":             None,
    }
    with open(cfg_dir / "result.json", "w") as f:
        json.dump(result, f, indent=2, default=str)

    wlog.info(
        f"[cfg {i}] DONE mi={mi}, tabu={tabu}, indeg={indeg} → "
        f"bic={bic:.1f}, edges={len(edges)}, "
        f"on_target={n_on_target}, restarts={pipeline.hcs_n_restarts}, "
        f"distinct={n_distinct}, elapsed={elapsed}s"
    )
    return result


# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------

if __name__ == "__main__":
    project_root = find_project_root()
    data_path    = project_root / "data" / "processed" / "feature_matrix.parquet"

    keys    = list(GRID.keys())
    configs = [dict(zip(keys, combo)) for combo in itertools.product(*GRID.values())]

    experiment_config = {
        "script":    "diff_analysis.scripts.megavul.bn1.tune_bn1",
        "n_workers": N_WORKERS,
        "grid":      GRID,
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
    logger.info(
        f"Grid search: {len(configs)} configs × HCS (max {HCS_MAX_RESTARTS} restarts each)"
        f" | {N_WORKERS} parallel workers"
    )

    # Create all config dirs upfront in the main process, then hand off to workers
    work_items = []
    for i, cfg in enumerate(configs, 1):
        cfg_dir = tracker.config_dir(i, cfg["mi_threshold"], cfg["tabu_length"], cfg["max_indegree"])
        work_items.append((i, cfg, df_raw, cfg_dir, i == 1))

    # Source: https://stackoverflow.com/a/40133278 (Tim, CC BY-SA 3.0)
    with Pool(processes=N_WORKERS) as pool:
        for _ in tqdm.tqdm(
            pool.imap_unordered(run_config, work_items),
            total=len(work_items),
            desc="configs",
        ):
            pass

    tracker.finalize()
