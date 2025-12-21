from primevul_analysis.extract import PrimeVulExtractor, DataPreparator
from primevul_analysis.coming_tool import ComingTool
from primevul_analysis.types import CodePair

from pathlib import Path
from typing import List
import logging

logging.basicConfig(
    level=logging.DEBUG,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

def extract_code_pairs(input_path: Path, output_path: Path) -> List[CodePair]:
    extractor = PrimeVulExtractor(input_path=input_path, output_path=output_path)
    code_pairs: List[CodePair] = extractor.extract_code_pairs()
    _ = extractor.save_code_pairs(code_pairs, format='csv', output_path=output_path / "code_pairs.csv")
    return code_pairs

def prepare_coming_data(code_pairs: List[CodePair], output_dir: Path) -> None:
    preparator = DataPreparator(output_dir=output_dir)
    _ = preparator.write_pairs(code_pairs)

def run_coming_diff_mode(pairs_root: Path):
    coming_tool = ComingTool()
    results = coming_tool.analyze_multiple_pairs(pairs_root)
    summary_df = coming_tool.summarize_results(results)
    summary_csv_path = Path(pairs_root) / "coming_analysis_summary.csv"
    summary_df.to_csv(summary_csv_path, index=False)
    logger.info("Coming analysis summary saved to %s", summary_csv_path)
    return results

def main():
    # Set project root
    project_root = Path(__file__).parent.parent.parent
    logger.info(f"Project root determined as: {project_root}")

    # Extract code pairs
    input_path = project_root / "PrimeVul_v0.1" / "primevul_train_paired.jsonl"
    output_path = project_root / "data"

    code_pairs = extract_code_pairs(input_path=input_path, output_path=output_path)
    logger.info("Code pair extraction completed.") 

    # Prepare extracted data for Coming
    coming_output_dir = project_root / "data" / "coming_data"
    prepare_coming_data(code_pairs=code_pairs, output_dir=coming_output_dir)

    logger.info("Finished preparing Coming data.")

    # Use ComingTool to analyze the prepared data
    pairs_root = coming_output_dir
    coming_results = run_coming_diff_mode(pairs_root=pairs_root)

    logger.info("Coming analysis completed.")
    
if __name__ == "__main__":
    main()