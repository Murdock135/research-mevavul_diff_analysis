from primevul_analysis.datapreparator.megavul import MegaVulDataPreparator
from primevul_analysis.types import MegaVCodePair
from primevul_analysis.utils.config_utils import find_project_root

import pandas as pd

from pathlib import Path
from typing import List
import logging
import ast

logging.basicConfig(
    level=logging.DEBUG,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler("primevul_analysis.log"),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)


def create_code_pairs_from_dataframe(df: pd.DataFrame) -> List[MegaVCodePair]:
    code_pairs: List[MegaVCodePair] = []
    for _, row in df.iterrows():
        pair = MegaVCodePair(
            id=str(row['id']),
            func_name=str(row['func_name']),
            vulnerable_code=row['vulnerable_code'],
            patched_code=row['patched_code'],
            cve_id=str(row['cve_id']),
            cwe_ids=ast.literal_eval(row['cwe_ids']) if isinstance(row['cwe_ids'], str) else row['cwe_ids'],
            commit_hash=str(row['commit_hash']),
            parent_commit_hash=str(row['parent_commit_hash']),
            repo_name=str(row['repo_name']),
            git_url=str(row['git_url']),
            file_path=str(row['file_path']),
        )
        code_pairs.append(pair)
    return code_pairs


def main():
    project_root = find_project_root()
    megavul_csv = project_root / "data" / "megavul_pairs.csv"
    df = pd.read_csv(megavul_csv)

    code_pairs: List[MegaVCodePair] = create_code_pairs_from_dataframe(df)

    output_dir = project_root / "data" / "megavul_pairs"
    preparator = MegaVulDataPreparator(output_dir=output_dir)
    preparator.write_pairs(code_pairs)
    logger.info(f"Written {len(code_pairs)} pairs to {output_dir}")


if __name__ == "__main__":
    main()
