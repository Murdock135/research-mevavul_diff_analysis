from primevul_analysis.extract import CodePairExtractor, ComingDataPreparator
from primevul_analysis.types import CodePair

from pathlib import Path
from typing import List
import logging

logging.basicConfig(
    level=logging.DEBUG,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

def main():
    project_root = Path(__file__).parent.parent.parent
    logger.info(f"Project root determined as: {project_root}")

    input_path = project_root / "PrimeVul_v0.1" / "primevul_train_paired.jsonl"
    output_path = project_root / "data"

    extractor = CodePairExtractor(input_path=input_path, output_path=output_path)
    code_pairs: List[CodePair] = extractor.extract_code_pairs()
    _ = extractor.save_code_pairs(code_pairs, format='csv', output_path=output_path / "code_pairs.csv")

    logger.info("Code pair extraction completed.") 

    # Prepare Coming data
    coming_output_dir = project_root / "data" / "coming_data"
    preparator = ComingDataPreparator(output_dir=coming_output_dir)
    _ = preparator.write_pairs(code_pairs)

    logger.info("Finished preparing Coming data.")


if __name__ == "__main__":
    main()