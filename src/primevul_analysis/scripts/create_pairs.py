from primevul_analysis.datapreparator.extract import DataPreparator
from primevul_analysis.types import PrimeVCodePair
from primevul_analysis.utils.config_utils import find_project_root

import pandas as pd

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

def create_code_pairs_from_dataframe(df: pd.DataFrame) -> List[PrimeVCodePair]:
    code_pairs: List[PrimeVCodePair] = []
    for i, row in df.iterrows():
        pair = PrimeVCodePair(
            id=str(row['id']),
            vulnerable_code=row['vulnerable_code'],
            patched_code=row['patched_code'],
            vulnerable_hash=row['vulnerable_hash'],
            patched_hash=row['patched_hash'],
            project=row['project'],
            cve=row['cve'],
            cwe=row['cwe'].strip("[]").replace("'", "").split(", ") if pd.notna(row['cwe']) else [],
            commit_id=str(row['commit_id'])
        )
        code_pairs.append(pair)

    return code_pairs

def write_pairs(code_pairs: List[PrimeVCodePair], output_dir: Path) -> None:
    preparator = DataPreparator(output_dir=output_dir)
    _ = preparator.write_pairs(code_pairs)

def main():
    # Load code pairs from CSV
    project_root = find_project_root()
    code_pairs_csv = project_root / "data" / "code_pairs.csv"
    df = pd.read_csv(code_pairs_csv)

    code_pairs: List[PrimeVCodePair] = create_code_pairs_from_dataframe(df)
    write_pairs(code_pairs=code_pairs, output_dir=project_root / "data" / "coming_data")
    
if __name__ == "__main__":
    main()