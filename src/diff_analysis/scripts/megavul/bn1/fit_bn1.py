"""
BN1 parameter fitting: loads a pipeline snapshot from learn_dag_isvul.py and fits CPDs.

Because the pipeline snapshot includes the preprocessed model_df, fitting uses
exactly the same rows and columns as structure learning — no mismatch possible.

Arguments
---------
--pipeline-file           str     (required)  Path to <stem>_pipeline.pkl from learn_dag_isvul.py
--equivalent-sample-size  float   5.0         ESS for BDeu Dirichlet prior
--output-dir              str     results     Output directory relative to project root data/

Outputs
-------
  <stem>_fitted.pkl        Pickled fitted DiscreteBayesianNetwork
  <stem>_cpd_is_vul.txt    CPD table for the is_vul node
"""

import argparse
import logging
from pathlib import Path

from diff_analysis.scripts.megavul.bn_utils import BNPipeline
from diff_analysis.utils.config_utils import find_project_root
from diff_analysis.utils.logging import setup_logging

setup_logging(level=logging.DEBUG)
logger = logging.getLogger(__name__)

TARGET_COL = "is_vul"


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="BN1 parameter fitting from a saved pipeline snapshot.",
        formatter_class=argparse.ArgumentDefaultsHelpFormatter,
    )
    parser.add_argument("--pipeline-file", type=str, required=True,
                        help="Path to <stem>_pipeline.pkl produced by learn_dag_isvul.py")
    parser.add_argument("--equivalent-sample-size", type=float, default=5.0,
                        help="Equivalent sample size for BDeu Dirichlet prior")
    parser.add_argument("--output-dir", type=str, default="results",
                        help="Output directory relative to project root data/")
    return parser.parse_args()


if __name__ == "__main__":
    args = parse_args()

    logger.info("Active arguments:")
    for k, v in vars(args).items():
        logger.info(f"  {k:<25} = {v}")

    project_root = find_project_root()
    out_dir = project_root / "data" / args.output_dir

    pipeline = BNPipeline.load(args.pipeline_file)
    stem = Path(args.pipeline_file).stem.removesuffix("_pipeline")

    pipeline.fit(equivalent_sample_size=args.equivalent_sample_size)
    pipeline.print_cpd(TARGET_COL)
    pipeline.save_model(out_dir, stem)
    pipeline.save_cpd(out_dir, stem, TARGET_COL)
