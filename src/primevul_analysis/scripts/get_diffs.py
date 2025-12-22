from primevul_analysis.difftools.gumtree import GumTreeTool, GumTreeToolPairsExecutor
from primevul_analysis.utils.config_utils import find_project_root
from primevul_analysis.utils.gumtree_utils import results_to_dataframe

from pathlib import Path
from typing import List
import logging

logging.basicConfig(
    level=logging.DEBUG,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler("primevul_analysis.log"),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)

def get_gumtree_diffs(pairs_root: Path):
    gumtree_tool = GumTreeTool()
    executor = GumTreeToolPairsExecutor(gumtree_tool=gumtree_tool)

    # Get all pair directories
    pair_dirs = [d for d in Path(pairs_root).iterdir() if d.is_dir()]
    logger.info("Found %d pair directories for GumTree analysis", len(pair_dirs))

    results = executor.batch_analyze_pair_dirs(
        pair_dirs,
        file1_stem="vulnerable",
        file2_stem="patched",
        suffix=".c",
        )
    df = results_to_dataframe(results)

    return df

def main():
    # Set project root
    project_root = find_project_root()
    logger.info(f"Project root determined as: {project_root}")

    # Define paths
    pairs_root = project_root / "data" / "coming_data"

    # Get GumTree diffs
    results_df = get_gumtree_diffs(pairs_root=pairs_root)

    logger.info("Got diff results from GumTree.")

    # Save GumTree results
    gumtree_results_path = project_root / "data" / "gumtree_diff_results.csv"
    results_df.to_csv(gumtree_results_path, index=False)
    logger.info("GumTree diff results saved to %s", gumtree_results_path)

    
if __name__ == "__main__":
    main()