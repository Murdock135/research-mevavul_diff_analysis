"""
Fit CPDs on the best DAG from data/results/paper/.

Selects the config with the highest BIC score from data/results/paper/,
or a specific one via --mi-group / --config-index.  Reconstructs the
preprocessing pipeline with the same hyperparameters used during the grid
search, loads the best-scoring edges from that config's hcs_restarts.jsonl,
fits CPDs with a BDeu prior, and saves the fitted model + edges.

Usage
-----
    # auto-select best config across all paper groups
    uv run python analysis/bn1/fit_bn1.py

    # pin to a specific MI group and config
    uv run python analysis/bn1/fit_bn1.py --mi-group mi100 --config-index 2

Outputs (under data/<output-dir>/<stem>/)
------------------------------------------
    <stem>_edges.csv / <stem>_edges.json   edge list
    <stem>_pipeline.pkl                    pipeline snapshot (model_df + edges)
    <stem>_fitted.pkl                      fitted DiscreteBayesianNetwork
    <stem>_cpd_is_vul.txt                  CPD for the target node

The stem encodes the selected config, e.g.
    bn1_hillclimb_mi50_tabu10_hcs
    bn1_hillclimb_mi100_tabu50_indeg3_hcs

Arguments
---------
--paper-dir       str    results/paper   Root of paper results (relative to data/)
--mi-group        str    None            Specific MI subdir, e.g. mi100 or mi50;
                                        if omitted, best config across all groups is used
--config-index    int    None            1-based config index within the MI group;
                                        if omitted, best config by BIC within the group is used
--ess             float  5.0             Equivalent sample size for BDeu prior
--output-dir      str    results/bn1     Output dir (relative to data/)
"""

import argparse
import json
import logging
import sys
from pathlib import Path

import pandas as pd

from megavul_diff_analysis.bn.pipeline import BNPipeline
from megavul_diff_analysis.utils.config_utils import find_project_root
from megavul_diff_analysis.utils.logging import setup_logging

# Import thresholds directly from tune_bn1 so they stay in sync automatically
sys.path.insert(0, str(Path(__file__).parent))
from tune_bn1 import FEATURE_THRESHOLD, SAMPLE_THRESHOLD  # noqa: E402

setup_logging(level=logging.INFO)
logger = logging.getLogger(__name__)

TARGET_COL = "is_vul"


# ---------------------------------------------------------------------------
# Config selection helpers
# ---------------------------------------------------------------------------

def _load_summary(mi_dir: Path) -> pd.DataFrame:
    """Return summary.csv for one MI group as a DataFrame."""
    return pd.read_csv(mi_dir / "summary.csv")


def find_best_config(paper_dir: Path, mi_group: str | None, config_index: int | None) -> dict:
    """
    Return a dict with keys: mi_group, config_index, mi_threshold, tabu_length,
    max_indegree, bic_score, config_dir.

    Selection logic:
      - If both mi_group and config_index are given → use that exact config.
      - If only mi_group is given → best BIC within that group.
      - If neither → best BIC across all groups.
    """
    if mi_group is not None:
        mi_dirs = [paper_dir / mi_group]
    else:
        # all subdirs that contain a summary.csv
        mi_dirs = [d for d in sorted(paper_dir.iterdir())
                   if d.is_dir() and (d / "summary.csv").exists()]

    if not mi_dirs:
        raise FileNotFoundError(f"No MI subdirs with summary.csv found under {paper_dir}")

    rows = []
    for mi_dir in mi_dirs:
        df = _load_summary(mi_dir)
        df["_mi_group"] = mi_dir.name
        df["_mi_dir"]   = str(mi_dir)
        rows.append(df)

    all_results = pd.concat(rows, ignore_index=True)
    all_results = all_results[all_results["error"].isna()]  # skip failed configs

    if config_index is not None:
        if mi_group is None:
            raise ValueError("--config-index requires --mi-group")
        row = all_results[all_results["config_index"] == config_index]
        if row.empty:
            raise ValueError(
                f"config_index={config_index} not found in {mi_group}/summary.csv"
            )
        row = row.iloc[0]
    else:
        row = all_results.loc[all_results["bic_score"].idxmax()]

    return {
        "mi_group":     row["_mi_group"],
        "config_index": int(row["config_index"]),
        "mi_threshold": int(row["mi_threshold"]),
        "tabu_length":  int(row["tabu_length"]),
        "max_indegree": None if pd.isna(row["max_indegree"]) else int(row["max_indegree"]),
        "bic_score":    float(row["bic_score"]),
        "config_dir":   Path(row["_mi_dir"]) / "configs" / _config_slug(
            int(row["config_index"]), int(row["mi_threshold"]),
            int(row["tabu_length"]),
            None if pd.isna(row["max_indegree"]) else int(row["max_indegree"]),
        ),
    }


def _config_slug(index: int, mi: int, tabu: int, indeg: int | None) -> str:
    indeg_str = "None" if indeg is None else str(indeg)
    return f"{index:03d}_mi{mi}_tabu{tabu}_indeg{indeg_str}"


def best_edges_from_restarts(config_dir: Path) -> list[tuple[str, str]]:
    """Return the edge list from the highest-scoring restart in hcs_restarts.jsonl."""
    restarts_file = config_dir / "hcs_restarts.jsonl"
    if not restarts_file.exists():
        raise FileNotFoundError(f"hcs_restarts.jsonl not found in {config_dir}")

    best_score, best_edges = None, None
    with open(restarts_file) as f:
        for line in f:
            entry = json.loads(line)
            if best_score is None or entry["score"] > best_score:
                best_score = entry["score"]
                best_edges = entry["edges"]

    if best_edges is None:
        raise ValueError(f"No restarts found in {restarts_file}")

    logger.info(f"Best restart score: {best_score:.4f}, edges: {len(best_edges)}")
    return [(u, v) for u, v in best_edges]


# ---------------------------------------------------------------------------
# Argument parsing
# ---------------------------------------------------------------------------

def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Fit BN1 CPDs on the best paper DAG.",
        formatter_class=argparse.ArgumentDefaultsHelpFormatter,
    )
    parser.add_argument(
        "--paper-dir", type=str, default="results/paper",
        help="Root of paper results, relative to data/",
    )
    parser.add_argument(
        "--mi-group", type=str, default=None,
        help="MI subdir to restrict search to, e.g. mi100 or mi50",
    )
    parser.add_argument(
        "--config-index", type=int, default=None,
        help="1-based config index within --mi-group (requires --mi-group)",
    )
    parser.add_argument(
        "--ess", type=float, default=5.0,
        help="Equivalent sample size for BDeu prior",
    )
    parser.add_argument(
        "--output-dir", type=str, default="results/bn1",
        help="Output directory relative to data/",
    )
    return parser.parse_args()


# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------

if __name__ == "__main__":
    args = parse_args()
    project_root = find_project_root()
    paper_dir    = project_root / "data" / args.paper_dir
    out_dir      = project_root / "data" / args.output_dir
    data_path    = project_root / "data" / "processed" / "megavul" / "feature_matrix.parquet"

    # ── 1. Find the best config ──────────────────────────────────────────
    cfg = find_best_config(paper_dir, args.mi_group, args.config_index)
    logger.info(
        f"Selected config: {cfg['mi_group']}/{cfg['config_index']:03d} "
        f"(mi={cfg['mi_threshold']}, tabu={cfg['tabu_length']}, "
        f"indeg={cfg['max_indegree']}, bic={cfg['bic_score']:.2f})"
    )
    logger.info(f"Config dir: {cfg['config_dir']}")

    # ── 2. Load best edges from hcs_restarts.jsonl ───────────────────────
    edges = best_edges_from_restarts(cfg["config_dir"])

    # ── 3. Rebuild preprocessing pipeline ────────────────────────────────
    logger.info(f"Loading feature matrix from {data_path}")
    df_raw = pd.read_parquet(data_path)

    pipeline = BNPipeline(df_raw, TARGET_COL)
    pipeline.preprocess(
        feature_threshold=FEATURE_THRESHOLD,
        sample_threshold=SAMPLE_THRESHOLD,
        mi_threshold=cfg["mi_threshold"],
    )
    pipeline.edges = edges
    logger.info(f"Edges loaded: {len(edges)} total")

    # ── 4. Fit CPDs ───────────────────────────────────────────────────────
    pipeline.fit(equivalent_sample_size=args.ess)
    pipeline.print_cpd()

    # ── 5. Save outputs ───────────────────────────────────────────────────
    indeg_tag = f"_indeg{cfg['max_indegree']}" if cfg["max_indegree"] is not None else ""
    stem    = f"bn1_hillclimb_mi{cfg['mi_threshold']}_tabu{cfg['tabu_length']}{indeg_tag}_hcs"
    run_dir = out_dir / stem

    pipeline.save_edges(run_dir, stem)
    pipeline.save_pipeline(run_dir / f"{stem}_pipeline.pkl")
    pipeline.save_model(run_dir, stem)
    pipeline.save_cpd(run_dir, stem)
    pipeline.save_cpd_json(run_dir, stem)

    logger.info(f"All outputs written to {run_dir}")
