"""
BN1 hyperparameter grid search.

Runs HillClimbSearch + HCS restarts for every combination in GRID,
records BIC-d score and HCS metadata, saves ranked results to CSV.

Usage
-----
    uv run python -m diff_analysis.scripts.megavul.bn1.tune_bn1

Outputs
-------
    data/results/tune_bn1_grid.csv   — all configs ranked by bic_score descending
"""

import itertools
import logging
import time
from pathlib import Path

import numpy as np
import pandas as pd
from pgmpy.estimators import BIC as BicScore

from diff_analysis.scripts.megavul.bn_utils import BNPipeline, META_COLS
from diff_analysis.utils.config_utils import find_project_root
from diff_analysis.utils.logging import setup_logging

setup_logging(level=logging.INFO)
logger = logging.getLogger(__name__)

TARGET_COL = "is_vul"

# ---------------------------------------------------------------------------
# Grid
# ---------------------------------------------------------------------------

GRID = {
    "mi_threshold": [50, 100, 200],
    "tabu_length":  [10, 50, 100],
    "max_indegree": [None, 3, 5],
}

# HCS params — kept fixed during grid search (looser c for speed)
HCS_DELTA        = 0.05
HCS_C            = 0.10   # looser than production (0.05) to keep each run bounded
HCS_MAX_RESTARTS = 50
SCORING          = "bic-d"
MAX_ITER         = 1_000_000
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
    out_path     = project_root / "data" / "results" / "tune_bn1_grid.csv"
    out_path.parent.mkdir(parents=True, exist_ok=True)

    logger.info(f"Loading feature matrix from {data_path}")
    df_raw = pd.read_parquet(data_path)

    keys   = list(GRID.keys())
    values = list(GRID.values())
    configs = [dict(zip(keys, combo)) for combo in itertools.product(*values)]
    logger.info(f"Grid search: {len(configs)} configs × HCS (max {HCS_MAX_RESTARTS} restarts each)")

    rows = []
    for i, cfg in enumerate(configs, 1):
        mi   = cfg["mi_threshold"]
        tabu = cfg["tabu_length"]
        indeg = cfg["max_indegree"]
        label = f"mi={mi}, tabu={tabu}, indeg={indeg}"
        logger.info(f"[{i}/{len(configs)}] {label}")

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
            )
        except Exception as e:
            logger.error(f"  FAILED: {e}")
            rows.append({
                "mi_threshold": mi, "tabu_length": tabu, "max_indegree": indeg,
                "n_restarts_used": None, "n_distinct_optima": None,
                "n_edges": None, "bic_score": None,
                "n_edges_on_target": None, "elapsed_s": round(time.time() - t0, 1),
                "error": str(e),
            })
            continue

        # Rebuild the best DAG object to score it (edges stored as tuples)
        from pgmpy.base import DAG as PgmDAG
        best_dag = PgmDAG()
        best_dag.add_nodes_from(pipeline.model_df.columns)
        best_dag.add_edges_from(pipeline.edges)
        bic = score_dag(best_dag, pipeline.model_df)

        n_on_target = sum(1 for u, v in pipeline.edges if TARGET_COL in (u, v))
        n_distinct   = len({r["cn"] for r in pipeline.hcs_history}) if pipeline.hcs_history else None

        row = {
            "mi_threshold":      mi,
            "tabu_length":       tabu,
            "max_indegree":      indeg,
            "n_restarts_used":   pipeline.hcs_n_restarts,
            "n_distinct_optima": len({
                frozenset(pipeline.edge_inclusion.keys())  # proxy; actual count via hcs history
            }) if pipeline.hcs_history else None,
            "n_edges":           len(pipeline.edges),
            "bic_score":         round(bic, 2),
            "n_edges_on_target": n_on_target,
            "elapsed_s":         round(time.time() - t0, 1),
            "error":             None,
        }
        rows.append(row)
        logger.info(
            f"  → bic={bic:.1f}, edges={len(pipeline.edges)}, "
            f"on_target={n_on_target}, restarts={pipeline.hcs_n_restarts}, "
            f"elapsed={row['elapsed_s']}s"
        )

    results = pd.DataFrame(rows).sort_values("bic_score", ascending=False).reset_index(drop=True)
    results.to_csv(out_path, index=False)
    logger.info(f"\nSaved grid results → {out_path}")

    best = results.iloc[0]
    logger.info(
        f"\nBest config: mi_threshold={best.mi_threshold}, "
        f"tabu_length={best.tabu_length}, max_indegree={best.max_indegree} "
        f"→ BIC={best.bic_score}, edges={best.n_edges}"
    )
