from diff_analysis.datapreparator.megavul import MegaVulDataPreparator
from diff_analysis.types import MegaVCodePair
from diff_analysis.utils.config_utils import find_project_root

import pandas as pd

from pathlib import Path
from typing import List
import logging

from diff_analysis.utils.logging import setup_logging
import ast

setup_logging(level=logging.DEBUG)
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
            file_name=str(row['file_name']),
            commit_date=int(row['commit_date']),
            commit_msg=str(row['commit_msg']),
            cvss_vector=str(row['cvss_vector']),
            cvss_base_score=float(row['cvss_base_score']),
            cvss_base_severity=str(row['cvss_base_severity']),
            cvss_is_v3=bool(row['cvss_is_v3']),
        )
        code_pairs.append(pair)
    return code_pairs


def main():
    project_root = find_project_root()
    megavul_csv = project_root / "data" / "processed" / "megavul_pairs.csv"
    df = pd.read_csv(megavul_csv)

    code_pairs: List[MegaVCodePair] = create_code_pairs_from_dataframe(df)

    output_dir = project_root / "data" / "interim" / "megavul_pairs"
    preparator = MegaVulDataPreparator(output_dir=output_dir)
    preparator.write_pairs(code_pairs)
    logger.info(f"Written {len(code_pairs)} pairs to {output_dir}")


if __name__ == "__main__":
    main()
