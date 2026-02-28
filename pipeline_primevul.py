"""
PrimeVul analysis pipeline — steps 1–3 (requires Docker / GumTree).

Steps:
  1. Extract code pairs from the PrimeVul JSONL → data/processed/code_pairs.csv
  2. Write pair directories for GumTree → data/interim/coming_data/
  3. Run GumTree on all pairs → data/interim/coming_data/pair_NNNNN/*.xml

Steps 4–6 (feature extraction, merge, Bayesian network) are run individually:
  uv run python -m diff_analysis.scripts.primevul.extract_features
  uv run python -m diff_analysis.scripts.primevul.merge_features_with_labels
  uv run python -m diff_analysis.scripts.primevul.bayesian_network_analysis
"""

from diff_analysis.datapreparator.primevul import PrimeVulExtractor, DataPreparator
from diff_analysis.difftools.gumtree import GumTreeTool, GumTreeToolPairsExecutor
from diff_analysis.types import PrimeVCodePair
from diff_analysis.utils.config_utils import find_project_root
from diff_analysis.utils.gumtree_utils import results_to_dataframe

from pathlib import Path
from typing import List
import logging

from diff_analysis.utils.logging import setup_logging

setup_logging(level=logging.INFO)
logger = logging.getLogger(__name__)


def extract_code_pairs(input_path: Path, output_path: Path) -> List[PrimeVCodePair]:
    extractor = PrimeVulExtractor(input_path=input_path, output_path=output_path)
    code_pairs: List[PrimeVCodePair] = extractor.extract_code_pairs()
    extractor.save_code_pairs(code_pairs, format='csv', output_path=output_path / "code_pairs.csv")
    return code_pairs


def prepare_pairs(code_pairs: List[PrimeVCodePair], output_dir: Path) -> None:
    preparator = DataPreparator(output_dir=output_dir)
    preparator.write_pairs(code_pairs)


def run_gumtree_diffs(pairs_root: Path) -> None:
    gumtree_tool = GumTreeTool()
    executor = GumTreeToolPairsExecutor(gumtree_tool=gumtree_tool)
    pair_dirs = [d for d in pairs_root.iterdir() if d.is_dir()]
    logger.info("Found %d pair directories for GumTree analysis", len(pair_dirs))
    results = executor.batch_analyze_pair_dirs(
        pair_dirs,
        file1_stem="vulnerable",
        file2_stem="patched",
        suffix=".c",
    )
    df = results_to_dataframe(results)
    out = pairs_root.parent / "gumtree_diff_results.csv"
    df.to_csv(out, index=False)
    logger.info("GumTree diff results saved to %s", out)


def main():
    project_root = find_project_root()

    input_path = project_root / "data" / "raw" / "PrimeVul_v0.1" / "primevul_train_paired.jsonl"
    processed_dir = project_root / "data" / "processed"
    interim_dir = project_root / "data" / "interim" / "coming_data"

    processed_dir.mkdir(parents=True, exist_ok=True)

    logger.info("Step 1: Extracting code pairs")
    code_pairs = extract_code_pairs(input_path=input_path, output_path=processed_dir)

    logger.info("Step 2: Writing pair directories")
    prepare_pairs(code_pairs=code_pairs, output_dir=interim_dir)

    logger.info("Step 3: Running GumTree diffs")
    run_gumtree_diffs(pairs_root=interim_dir)

    logger.info("Pipeline complete.")


if __name__ == "__main__":
    main()
