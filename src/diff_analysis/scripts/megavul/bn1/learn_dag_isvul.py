"""
BN1 structure learning: code change features -> is_vul.

Learns a DAG, saves edges and a pipeline snapshot (pipeline.pkl) that
preserves the preprocessed model_df so fit_bn1.py can fit without
re-running preprocessing.

Arguments
---------
--method              str     hillclimb   Structure learning method: 'hillclimb' or 'mmhc'
--scoring-method      str     None        Scoring function; defaults to 'bdeu' (mmhc) or 'bic-d' (hillclimb)
--significance-level  float   0.01        Significance level for MMHC independence tests
--ci-test             str     chi_square  CI test for MMHC skeleton phase ('chi_square', 'g_sq', 'log_likelihood')
--mi-threshold        int     None        Retain top N features by MI before learning
--tabu-length         int     10          Tabu length for HillClimbSearch
--max-iter            int     1000        Max hill-climbing iterations (hillclimb only)
--max-indegree        int     None        Maximum parents per node (unrestricted if unset)
--feature-threshold   float   0.0001      Min fraction of samples where a feature must be non-zero
--sample-threshold    float   0.0         Min fraction of non-zero features a sample must have
--output-dir          str     results     Output directory relative to project root data/

Outputs
-------
  <stem>_edges.csv        Edge list
  <stem>_edges.json       Edge list (JSON)
  <stem>_pipeline.pkl     Pipeline snapshot for fit_bn1.py
"""

import argparse
import logging

import pandas as pd

from diff_analysis.scripts.megavul.bn_utils import BNPipeline
from diff_analysis.utils.config_utils import find_project_root
from diff_analysis.utils.logging import setup_logging

setup_logging(level=logging.DEBUG)
logger = logging.getLogger(__name__)

TARGET_COL = "is_vul"


def build_stem(method: str, mi_threshold: int | None) -> str:
    mi_tag = f"_mi{mi_threshold}" if mi_threshold is not None else ""
    return f"bn1_{method}{mi_tag}"


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="BN1 structure learning (features -> is_vul).",
        formatter_class=argparse.ArgumentDefaultsHelpFormatter,
    )
    parser.add_argument("--method", default="hillclimb", choices=["hillclimb", "mmhc"])
    parser.add_argument("--scoring-method", type=str, default=None,
                        help="Scoring function; defaults to 'bdeu' (mmhc) or 'bic-d' (hillclimb)")
    parser.add_argument("--significance-level", type=float, default=0.01)
    parser.add_argument("--ci-test", type=str, default="chi_square",
                        choices=["chi_square", "g_sq", "log_likelihood"])
    parser.add_argument("--mi-threshold", type=int, default=None)
    parser.add_argument("--tabu-length", type=int, default=10)
    parser.add_argument("--max-iter", type=int, default=1000)
    parser.add_argument("--max-indegree", type=int, default=None)
    parser.add_argument("--feature-threshold", type=float, default=0.0001)
    parser.add_argument("--sample-threshold", type=float, default=0.0)
    parser.add_argument("--output-dir", type=str, default="results")
    return parser.parse_args()


if __name__ == "__main__":
    args = parse_args()

    logger.info("Active arguments:")
    for k, v in vars(args).items():
        logger.info(f"  {k:<25} = {v}")

    project_root = find_project_root()
    df = pd.read_parquet(project_root / "data" / "processed" / "feature_matrix.parquet")

    pipeline = BNPipeline(df, TARGET_COL)
    pipeline.preprocess(
        feature_threshold=args.feature_threshold,
        sample_threshold=args.sample_threshold,
        mi_threshold=args.mi_threshold,
    ).learn_structure(
        method=args.method,
        scoring_method=args.scoring_method,
        significance_level=args.significance_level,
        ci_test=args.ci_test,
        tabu_length=args.tabu_length,
        max_iter=args.max_iter,
        max_indegree=args.max_indegree,
    )

    print("\nEdge list:")
    for u, v in pipeline.edges:
        print(f"  {u} -> {v}")

    stem = build_stem(args.method, args.mi_threshold)
    out_dir = project_root / "data" / args.output_dir
    pipeline.save_edges(out_dir, stem)
    pipeline.save_pipeline(out_dir / f"{stem}_pipeline.pkl")
