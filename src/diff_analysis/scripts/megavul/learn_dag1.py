"""
Learn a DAG structure from the MegaVul feature matrix using MMHC or HillClimb.

Arguments
---------
--method              str     mmhc        Structure learning method: 'mmhc' or 'hillclimb'
--significance-level  float   0.01        Significance level for MMHC independence tests
--mi-threshold        int     None        If set, retain top N features by mutual information before learning
--tabu-length         int     10          Tabu length for HillClimbSearch
--max-indegree        int     None        Maximum number of parents per node (unrestricted if not set)
--output-dir          str     results     Directory (relative to project root) to save outputs
--feature-threshold   float   0.0001      Min fraction of samples where a feature must be non-zero to be kept
--sample-threshold    float   0.0         Min fraction of non-zero features a sample must have to be kept

Outputs
-------
Filenames reflect the method and options, e.g.:
  dag1_mmhc_edges.csv
  dag1_hillclimb_mi50_edges.csv
"""

import argparse
import json
import logging
import time

import pandas as pd
from sklearn.feature_selection import mutual_info_classif

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


def select_top_mi_features(X: pd.DataFrame, y: pd.Series, k: int) -> list[str]:
    """Return names of top-k features ranked by mutual information with y."""
    mi = mutual_info_classif(X.values, y.values, discrete_features=True, random_state=0)
    mi_series = pd.Series(mi, index=X.columns).sort_values(ascending=False)
    selected = mi_series.head(k).index.tolist()
    logger.info(f"MI feature selection: retained {len(selected)} / {len(X.columns)} features (top {k})")
    logger.debug(f"Top MI features:\n{mi_series.head(min(k, 15)).to_string()}")
    return selected


def build_output_stem(method: str, mi_threshold: int | None) -> str:
    mi_tag = f"_mi{mi_threshold}" if mi_threshold is not None else ""
    return f"dag1_{method}{mi_tag}"


def learn_dag(
    df: pd.DataFrame,
    *,
    method: str = "mmhc",
    significance_level: float = 0.01,
    mi_threshold: int | None = None,
    tabu_length: int = 10,
    max_indegree: int | None = None,
    feature_threshold: float = 0.0001,
    sample_threshold: float = 0.0,
) -> list[tuple[str, str]]:
    feature_cols = [c for c in df.columns if c not in META_COLS]

    # Preprocessing: remove sparse samples, then sparse features
    model_df = df[feature_cols + ["is_vul"]].copy()
    model_df = threshold_samples(model_df, threshold=sample_threshold)
    feat_df = threshold_features(model_df.drop(columns=["is_vul"]), threshold=feature_threshold)
    model_df = feat_df.join(model_df["is_vul"]).astype(int)
    logger.info(f"After sparsity filter: {model_df.shape} ({len(feat_df.columns)} features)")
    breakpoint()

    # Optional MI feature selection
    if mi_threshold is not None:
        selected = select_top_mi_features(
            model_df.drop(columns=["is_vul"]), model_df["is_vul"], k=mi_threshold
        )
        model_df = model_df[selected + ["is_vul"]]
        logger.info(f"After MI selection: {model_df.shape}")

    # Structure learning
    kwargs: dict = {}
    if max_indegree is not None:
        kwargs["max_indegree"] = max_indegree

    if method == "mmhc":
        from pgmpy.estimators import MmhcEstimator
        logger.info(f"Starting MMHC (significance_level={significance_level})...")
        t0 = time.time()
        estimator = MmhcEstimator(model_df)

        try:
            learned_dag = estimator.estimate(
                scoring_method="bdeu",
                significance_level=significance_level,
                tabu_length=tabu_length,
                **kwargs,
            )
        except Exception as e:
            logger.error(f"MMHC estimation failed: {e}")
            raise

    elif method == "hillclimb":
        from pgmpy.estimators import HillClimbSearch
        logger.info(f"Starting HillClimbSearch (tabu_length={tabu_length})...")
        t0 = time.time()
        estimator = HillClimbSearch(model_df)
        learned_dag = estimator.estimate(
            scoring_method="bic-d",
            tabu_length=tabu_length,
            **kwargs,
        )
    else:
        raise ValueError(f"Unknown method: {method!r}. Choose 'mmhc' or 'hillclimb'.")

    elapsed = time.time() - t0
    logger.info(f"Training complete in {elapsed:.1f}s")

    edges = list(learned_dag.edges())
    is_vul_edges = [(u, v) for u, v in edges if u == "is_vul" or v == "is_vul"]
    logger.info(f"Total edges: {len(edges)}")
    logger.info(f"Edges connecting to is_vul ({len(is_vul_edges)}): {is_vul_edges}")

    return edges


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Learn a DAG from the MegaVul feature matrix.",
        formatter_class=argparse.ArgumentDefaultsHelpFormatter,
    )
    parser.add_argument("--method", type=str, default="mmhc",
                        choices=["mmhc", "hillclimb"],
                        help="Structure learning method")
    parser.add_argument("--significance-level", type=float, default=0.01,
                        help="Significance level for MMHC independence tests")
    parser.add_argument("--mi-threshold", type=int, default=None,
                        help="Retain top N features by mutual information before learning")
    parser.add_argument("--tabu-length", type=int, default=10,
                        help="Tabu length for HillClimbSearch")
    parser.add_argument("--max-indegree", type=int, default=None,
                        help="Maximum number of parents per node (unrestricted if not set)")
    parser.add_argument("--output-dir", type=str, default="results",
                        help="Output directory relative to project root data/")
    parser.add_argument("--feature-threshold", type=float, default=0.0001,
                        help="Min fraction of samples where a feature must be non-zero to be kept")
    parser.add_argument("--sample-threshold", type=float, default=0.0,
                        help="Min fraction of non-zero features a sample must have to be kept")
    return parser.parse_args()


if __name__ == "__main__":
    args = parse_args()

    logger.info("Active arguments:")
    logger.info(f"  method            = {args.method}")
    logger.info(f"  significance-level = {args.significance_level}")
    logger.info(f"  mi-threshold      = {args.mi_threshold}")
    logger.info(f"  tabu-length       = {args.tabu_length}")
    logger.info(f"  max-indegree       = {args.max_indegree}")
    logger.info(f"  output-dir         = {args.output_dir}")
    logger.info(f"  feature-threshold  = {args.feature_threshold}")
    logger.info(f"  sample-threshold   = {args.sample_threshold}")

    project_root = find_project_root()
    path = project_root / "data" / "processed" / "feature_matrix.parquet"
    df = pd.read_parquet(path)

    edges = learn_dag(
        df,
        method=args.method,
        significance_level=args.significance_level,
        mi_threshold=args.mi_threshold,
        tabu_length=args.tabu_length,
        max_indegree=args.max_indegree,
        feature_threshold=args.feature_threshold,
        sample_threshold=args.sample_threshold,
    )

    print("\nEdge list:")
    for u, v in edges:
        print(f"  {u} -> {v}")

    stem = build_output_stem(args.method, args.mi_threshold)
    out_dir = project_root / "data" / args.output_dir
    out_dir.mkdir(parents=True, exist_ok=True)

    csv_path = out_dir / f"{stem}_edges.csv"
    json_path = out_dir / f"{stem}_edges.json"

    pd.DataFrame(edges, columns=["source", "target"]).to_csv(csv_path, index=False)
    logger.info(f"Saved CSV:  {csv_path}")

    with open(json_path, "w") as f:
        json.dump([{"source": u, "target": v} for u, v in edges], f, indent=2)
    logger.info(f"Saved JSON: {json_path}")
