from pathlib import Path
import tempfile
import os
from dataclasses import dataclass
import json
from typing import List, Optional

import pandas as pd

from primevul_analysis.types import CodePair
from primevul_analysis.logging.logger import debug_here

class CodePairExtractor:
    def __init__(self, input_path: Path, output_path: Path) -> None:
        self.path = Path(input_path)
        self.output_path = Path(output_path)

    def _load_data(self) -> pd.DataFrame:
        self.data = []
        with open(self.path, 'r') as f:
            for line in f:
                self.data.append(json.loads(line))
        self.df = pd.DataFrame(self.data)
        return self.df
    
    def extract_code_pairs(self) -> List[CodePair]:
        df = self._load_data()
        code_pairs = []
        
        for commit_id, group in df.groupby('commit_id'):
            debug_here(f"Check type(commit_id): {type(commit_id)}")
            
            vuln_group: pd.DataFrame = group.loc[group['target'] == 1]
            patched_group: pd.DataFrame = group.loc[group['target'] == 0]

            # Only keep if both vulnerable and patched code exist
            if len(vuln_group) > 0 and len(patched_group) > 0:
                vuln_row: pd.Series = vuln_group.iloc[0]
                patched_row: pd.Series = patched_group.iloc[0]

                code_pair = CodePair(
                    id=vuln_row['idx'],
                    vulnerable_code=vuln_row['func'],
                    patched_code=patched_row['func'],
                    cve=vuln_row.get('cve', ''),
                    cwe=vuln_row.get('cwe', ''),
                    commit_id=commit_id,
                    project=vuln_row.get('project', ''),
                    vulnerable_hash=vuln_row.get('hash', None),
                    patched_hash=patched_row.get('hash', None),
                )
                code_pairs.append(code_pair)

        self.code_pairs = code_pairs
        return code_pairs
    
    def save_code_pairs(self, code_pairs: List[CodePair], format: str = 'csv', output_path: Path = None) -> pd.DataFrame:
        """
        Save the extracted code pairs to the specified format (csv or json).
        """
        if not self.code_pairs:
            raise ValueError("No code pairs to save. Please run extract_code_pairs() first.")
        
        output_path = output_path or self.output_path / f'code_pairs.{format}' # Default to self.output_path if not provided
        os.makedirs(output_path.parent, exist_ok=True)

        df = pd.DataFrame([cp.model_dump() for cp in code_pairs])

        # Save in the specified format
        if format == 'csv':
            df.to_csv(output_path, index=False)
        elif format == 'json':
            df.to_json(output_path, orient='records', lines=True)
        elif format == 'parquet':
            df.to_parquet(output_path, index=False)
        else:
            raise ValueError(f"Unsupported format: {format}. Supported formats are: csv, json, parquet.")
        
        return df

class ComingDataPreparator:
    def __init__(self, input_dir: Path, output_dir: Path) -> None:
        self.input_dir = Path(input_dir)
        self.output_dir = Path(output_dir)
        self.pair_dirs: List[Path] = []

    def write_pairs(self, pairs: List[CodePair], output_path: Optional[str] = None):
        output_dir = Path(output_path) if output_path is not None else self.output_dir
        output_dir.mkdir(parents=True, exist_ok=True)

        for index, pair in enumerate(pairs):
            pair_dir = self.write_single_pair(pair, index, output_dir)
            self.pair_dirs.append(pair_dir)

        return self.pair_dirs

    def write_single_pair(self, pair: CodePair, index: int, output_dir: Optional[Path] = None) -> Path:
        output_dir = output_dir if output_dir is not None else self.output_dir

        pair_dir = output_dir / f"pair_{index:05d}"
        pair_dir.mkdir(parents=True, exist_ok=True)

        vuln_file = pair_dir / "vulnerable.c"
        patched_file = pair_dir / "patched.c"

        # Write Vulnerable code
        with open(vuln_file, "w", encoding="utf-8") as f:
            f.write(pair.vulnerable_code)
        
        # Write Patched code
        with open(patched_file, "w", encoding="utf-8") as f:
            f.write(pair.patched_code)

        # Write metadata
        meta_file = pair_dir / "metadata.json"
        metadata = {
            "id": pair.id,
            "cve": pair.cve,
            "cwe": pair.cwe,
            "commit_id": pair.commit_id,
            "project": pair.project,
            "vulnerable_hash": pair.vulnerable_hash,
            "patched_hash": pair.patched_hash,
        }
        with open(meta_file, "w", encoding="utf-8") as f:
            json.dump(metadata, f, indent=4)

        return pair_dir