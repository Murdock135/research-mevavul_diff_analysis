from pathlib import Path
import tempfile
import os
from dataclasses import dataclass
import json
import logging
from typing import List, Optional, Dict

import pandas as pd

from primevul_analysis.types import CodePair

logger = logging.getLogger(__name__)

class CodePairExtractor:
    def __init__(self, input_path: Path, output_path: Path) -> None:
        self.path = Path(input_path)
        self.output_path = Path(output_path)

    def _load_data(self) -> pd.DataFrame:
        if not self.path.exists():
            raise FileNotFoundError(f"Data file not found: {self.path}")
        
        logger.info(f"Loading data from {self.path}")
        
        self.data = []
        with open(self.path, 'r') as f:
            for line in f:
                if line.strip():
                    self.data.append(json.loads(line))
        
        if not self.data:
            raise ValueError(f"No data loaded from {self.path}")
        
        self.df = pd.DataFrame(self.data)
        logger.info(f"Loaded {len(self.df)} records")
        return self.df
    
    def extract_code_pairs(self) -> List[CodePair]:
        df = self._load_data()
        code_pairs: List[CodePair] = []
        
        total_commits = df['commit_id'].nunique()
        logger.info(f"Extracting pairs from {total_commits} unique commits")
        
        skipped = 0
        failed = 0
        
        for i, (commit_id, group) in enumerate(df.groupby('commit_id')):
            try:
                vuln_group = group[group['target'] == 1]
                patched_group = group[group['target'] == 0]

                # Only keep if exactly one vulnerable and one patched exist
                if len(vuln_group) != 1 or len(patched_group) != 1:
                    skipped += 1
                    continue
                
                vuln_row = vuln_group.iloc[0]
                patched_row = patched_group.iloc[0]

                code_pair = CodePair(
                    id=str(vuln_row['idx']),
                    vulnerable_code=vuln_row['func'],
                    patched_code=patched_row['func'],
                    cve=vuln_row.get('cve', ''),
                    cwe=vuln_row.get('cwe', ''),
                    commit_id=commit_id,
                    project=vuln_row.get('project', ''),
                    vulnerable_hash=vuln_row.get('func_hash'),
                    patched_hash=patched_row.get('func_hash'),
                )
                code_pairs.append(code_pair)
                
            except Exception as e:
                logger.warning(f"Failed to extract pair for commit {commit_id}: {e}")
                failed += 1
                continue
            
            # Progress logging
            if (i + 1) % 100 == 0:
                logger.info(f"  Processed {i + 1}/{total_commits} commits")

        self.code_pairs = code_pairs
        logger.info(f"Extracted {len(code_pairs)} complete pairs (skipped {skipped}, failed {failed})")
        return code_pairs
    
    def save_code_pairs(self, code_pairs: List[CodePair], format: str = 'csv', output_path: Optional[Path] = None) -> pd.DataFrame:
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
        
        logger.info(f"Saved {len(code_pairs)} pairs to {output_path}")
        return df
    
    def get_statistics(self) -> Dict:
        """Get summary statistics about extracted pairs."""
        if not self.code_pairs:
            return {}
        
        df = pd.DataFrame([p.model_dump() for p in self.code_pairs])
        
        return {
            'total_pairs': len(self.code_pairs),
            'unique_cwes': df['cwe'].nunique(),
            'unique_projects': df['project'].nunique(),
            'top_cwes': df['cwe'].value_counts().head(10).to_dict(),
        }
    
    def filter_by_cwe(self, cwe_list: List[str]) -> List[CodePair]:
        """Filter pairs by CWE type."""
        return [p for p in self.code_pairs if p.cwe in cwe_list]
    
    def get_top_cwes(self, n: int = 5) -> List[str]:
        """Get top N most common CWEs."""
        if not self.code_pairs:
            return []
        df = pd.DataFrame([p.model_dump() for p in self.code_pairs])
        return df['cwe'].value_counts().head(n).index.tolist()

class ComingDataPreparator:
    def __init__(self, output_dir: Path) -> None:
        self.output_dir = Path(output_dir)
        self.pair_dirs: List[Path] = []

    def write_pairs(self, pairs: List[CodePair], output_path: Optional[str] = None) -> List[Path]:
        output_dir = Path(output_path) if output_path is not None else self.output_dir
        output_dir.mkdir(parents=True, exist_ok=True)

        logger.info(f"Writing {len(pairs)} code pairs to {output_dir}")

        for index, pair in enumerate(pairs):
            pair_dir = self.write_single_pair(pair, index, output_dir)
            self.pair_dirs.append(pair_dir)
            
            if (index + 1) % 100 == 0:
                logger.info(f"  Written {index + 1}/{len(pairs)} pairs")

        logger.info(f"Completed! Files in {output_dir}")
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