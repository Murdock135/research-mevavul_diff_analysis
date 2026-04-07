from megavul_diff_analysis.datapreparator.megavul import MegaVulExtractor
from megavul_diff_analysis.types import MegaVCodePair
from megavul_diff_analysis.utils.config_utils import find_project_root

from pathlib import Path
from typing import List
import logging

from megavul_diff_analysis.utils.logging import setup_logging

setup_logging(level=logging.DEBUG)
logger = logging.getLogger(__name__)

def extract_code_pairs(input_path: Path, output_path: Path) -> List[MegaVCodePair]:
    extractor = MegaVulExtractor(input_path=input_path, output_path=output_path)
    code_pairs: List[MegaVCodePair] = extractor.extract_code_pairs()
    _ = extractor.save_code_pairs(code_pairs, format='csv', output_path=output_path / "megavul_pairs.csv")
    return code_pairs

def main():
    project_root = find_project_root()
    logger.info(f"Project root determined as: {project_root}")

    input_path = project_root / "data" / "raw" / "megavul" / "cve_with_graph_abstract_commit.json"
    output_path = project_root / "data" / "processed" / "megavul"

    _ = extract_code_pairs(input_path=input_path, output_path=output_path)
    logger.info("MegaVul extraction completed.")

if __name__ == "__main__":
    main()
