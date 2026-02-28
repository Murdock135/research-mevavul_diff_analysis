from diff_analysis.datapreparator.primevul import PrimeVulExtractor
from diff_analysis.types import PrimeVCodePair
from diff_analysis.utils.config_utils import find_project_root

from pathlib import Path
from typing import List
import logging

logging.basicConfig(
    level=logging.DEBUG,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler("diff_analysis.log"),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)

def extract_code_pairs(input_path: Path, output_path: Path) -> List[PrimeVCodePair]:
    extractor = PrimeVulExtractor(input_path=input_path, output_path=output_path)
    code_pairs: List[PrimeVCodePair] = extractor.extract_code_pairs()
    _ = extractor.save_code_pairs(code_pairs, format='csv', output_path=output_path / "code_pairs.csv")
    return code_pairs

def main():
    # Set project root
    project_root = find_project_root()
    logger.info(f"Project root determined as: {project_root}")

    # Extract code pairs
    input_path = project_root / "data" / "raw" / "PrimeVul_v0.1" / "primevul_train_paired.jsonl"
    output_path = project_root / "data" / "processed"

    _ = extract_code_pairs(input_path=input_path, output_path=output_path)
    logger.info("Code pair extraction completed.") 
    
if __name__ == "__main__":
    main()