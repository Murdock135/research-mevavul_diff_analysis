import json
import logging
import time

import pandas as pd
from pgmpy.estimators import MmhcEstimator

from diff_analysis.utils.config_utils import find_project_root
from diff_analysis.utils.logging import setup_logging

setup_logging(level=logging.DEBUG)
logger = logging.getLogger(__name__)

META_COLS = [
    "is_vul", "id", "func_name", "cve_id", "cwe_ids", "commit_hash",
    "parent_commit_hash", "repo_name", "git_url", "file_path", "file_name",
    "commit_date", "commit_msg", "cvss_vector", "cvss_base_score",
    "cvss_base_severity", "cvss_is_v3",
]


def threshold_features(df, threshold=0.05):
    """Remove features that are non-zero in less than `threshold` fraction of the samples."""
    if threshold > 1:
        logger.warning("Threshold is greater than 1. Interpreting as percentage of samples.")
        threshold = threshold / 100
        logger.debug(f"Using threshold: {threshold}")

    row_n = df.shape[0]
    feature_mask = (df != 0).sum(axis=0) / row_n >= threshold
    return df.loc[:, feature_mask]


def threshold_samples(df, threshold=0.0):
    """
    Remove samples that have less than `threshold` (default=0.0) fraction of non-zero features.

    By default, this function removes samples that have no non-zero features.
    """
    if threshold > 1:
        logger.warning("Threshold is greater than 1. Interpreting as percentage of features.")
        threshold = threshold / 100
        logger.debug(f"Using threshold: {threshold}")

    col_n = df.shape[1]
    sample_mask = (df != 0).sum(axis=1) / col_n >= threshold
    return df.loc[sample_mask, :]


def learn_dag(df: pd.DataFrame) -> list[tuple[str, str]]:
    feature_cols = [c for c in df.columns if c not in META_COLS]

    # Preprocessing
    model_df = df[feature_cols + ["is_vul"]].copy()
    model_df = threshold_samples(model_df, threshold=0.0)
    feat_df = threshold_features(model_df.drop(columns=["is_vul"]), threshold=0.0001)
    model_df = feat_df.join(model_df["is_vul"])
    model_df = model_df.astype(int)
    logger.info(f"Training data shape after preprocessing: {model_df.shape} ({len(feat_df.columns)} features)")

    # Structure learning
    logger.info("Starting MMHC with BDeu scoring...")
    t0 = time.time()
    mmhc = MmhcEstimator(model_df)
    learned_dag = mmhc.estimate() # Default scoring method is BDeu
    elapsed = time.time() - t0
    logger.info(f"Training complete in {elapsed:.1f}s")

    edges = list(learned_dag.edges())
    is_vul_edges = [(u, v) for u, v in edges if u == "is_vul" or v == "is_vul"]
    logger.info(f"Total edges: {len(edges)}")
    logger.info(f"Edges connecting to is_vul ({len(is_vul_edges)}): {is_vul_edges}")

    return edges


if __name__ == "__main__":
    project_root = find_project_root()
    path = project_root / "data" / "processed" / "feature_matrix.parquet"
    df = pd.read_parquet(path)

    edges = learn_dag(df)

    print("\nEdge list:")
    for u, v in edges:
        print(f"  {u} -> {v}")

    out_dir = project_root / "data" / "results"
    out_dir.mkdir(parents=True, exist_ok=True)

    pd.DataFrame(edges, columns=["source", "target"]).to_csv(out_dir / "bn1_edges.csv", index=False)
    logger.info(f"Saved CSV: {out_dir / 'bn1_edges.csv'}")

    with open(out_dir / "bn1_edges.json", "w") as f:
        json.dump([{"source": u, "target": v} for u, v in edges], f, indent=2)
    logger.info(f"Saved JSON: {out_dir / 'bn1_edges.json'}")
