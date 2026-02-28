from primevul_analysis.datapreparator.megavul import MegaVulExtractor
from primevul_analysis.types import MegaVCodePair
from primevul_analysis.utils.config_utils import find_project_root

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

def extract_code_pairs(input_path: Path, output_path: Path) -> List[MegaVCodePair]:
    extractor = MegaVulExtractor(input_path=input_path, output_path=output_path)
    code_pairs: List[MegaVCodePair] = extractor.extract_code_pairs()
    _ = extractor.save_code_pairs(code_pairs, format='csv', output_path=output_path / "megavul_pairs.csv")
    return code_pairs

def main():
    project_root = find_project_root()
    logger.info(f"Project root determined as: {project_root}")

    input_path = project_root / "megavul" / "cve_with_graph_abstract_commit.json"
    output_path = project_root / "data"

    _ = extract_code_pairs(input_path=input_path, output_path=output_path)
    logger.info("MegaVul extraction completed.")

if __name__ == "__main__":
    main()
