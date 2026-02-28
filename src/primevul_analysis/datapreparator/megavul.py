import json
import logging
import os
from pathlib import Path
from typing import Dict, List, Optional

import pandas as pd
from tqdm import tqdm

from primevul_analysis.types import MegaVCodePair

logger = logging.getLogger(__name__)


class MegaVulExtractor:
    """Extract vulnerable/patched function pairs from the MegaVul dataset."""

    def __init__(self, input_path: Path, output_path: Path) -> None:
        self.path = Path(input_path)
        self.output_path = Path(output_path)
        self.code_pairs: List[MegaVCodePair] = []

    def _load_data(self) -> list:
        if not self.path.exists():
            raise FileNotFoundError(f"Data file not found: {self.path}")

        logger.info(f"Loading data from {self.path}")
        with open(self.path, "r") as f:
            data = json.load(f)

        if not data:
            raise ValueError(f"No data loaded from {self.path}")

        logger.info(f"Loaded {len(data)} CVE entries")
        return data

    def extract_code_pairs(self) -> List[MegaVCodePair]:
        data = self._load_data()
        code_pairs: List[MegaVCodePair] = []

        skipped = 0
        failed = 0

        for cve_entry in tqdm(data, desc="Extracting MegaVul pairs"):
            cve_id: str = cve_entry.get("cve_id", "")
            cwe_ids: List[str] = cve_entry.get("cwe_ids", [])

            for commit in cve_entry.get("commits", []):
                commit_hash: str = commit.get("commit_hash", "")
                parent_commit_hash: str = commit.get("parent_commit_hash", "")
                repo_name: str = commit.get("repo_name", "")
                git_url: str = commit.get("git_url", "")

                for file_entry in commit.get("files", []):
                    file_path: str = file_entry.get("file_path", "")

                    for idx, func in enumerate(file_entry.get("vulnerable_functions", [])):
                        try:
                            func_name: str = func.get("func_name", "")
                            func_before: str = func.get("func_before", "")
                            func_after: str = func.get("func_after", "")

                            if not func_before or not func_after:
                                skipped += 1
                                continue

                            pair = MegaVCodePair(
                                id=f"{commit_hash}_{func_name}_{idx}",
                                func_name=func_name,
                                vulnerable_code=func_before,
                                patched_code=func_after,
                                cve_id=cve_id,
                                cwe_ids=cwe_ids,
                                commit_hash=commit_hash,
                                parent_commit_hash=parent_commit_hash,
                                repo_name=repo_name,
                                git_url=git_url,
                                file_path=file_path,
                            )
                            code_pairs.append(pair)

                        except Exception as e:
                            logger.warning(
                                f"Failed to extract pair for commit {commit_hash}, "
                                f"func {func.get('func_name', '?')}: {e}"
                            )
                            failed += 1
                            continue

        self.code_pairs = code_pairs
        logger.info(
            f"Extracted {len(code_pairs)} pairs (skipped {skipped}, failed {failed})"
        )
        return code_pairs

    def save_code_pairs(
        self,
        code_pairs: List[MegaVCodePair],
        format: str = "csv",
        output_path: Optional[Path] = None,
    ) -> pd.DataFrame:
        if not self.code_pairs:
            raise ValueError("No code pairs to save. Run extract_code_pairs() first.")

        output_path = output_path or self.output_path / f"megavul_pairs.{format}"
        os.makedirs(output_path.parent, exist_ok=True)

        df = pd.DataFrame([cp.model_dump() for cp in code_pairs])

        if format == "csv":
            df.to_csv(output_path, index=False)
        elif format == "json":
            df.to_json(output_path, orient="records", lines=True)
        elif format == "parquet":
            df.to_parquet(output_path, index=False)
        else:
            raise ValueError(
                f"Unsupported format: {format}. Supported: csv, json, parquet."
            )

        logger.info(f"Saved {len(code_pairs)} pairs to {output_path}")
        return df

    def get_statistics(self) -> Dict:
        if not self.code_pairs:
            return {}

        df = pd.DataFrame([p.model_dump() for p in self.code_pairs])
        return {
            "total_pairs": len(self.code_pairs),
            "unique_cves": df["cve_id"].nunique(),
            "unique_repos": df["repo_name"].nunique(),
            "top_cwe_ids": (
                df["cwe_ids"]
                .explode()
                .value_counts()
                .head(10)
                .to_dict()
            ),
        }


class MegaVulDataPreparator:
    def __init__(self, output_dir: Path) -> None:
        self.output_dir = Path(output_dir)
        self.pair_dirs: List[Path] = []

    def write_pairs(self, pairs: List[MegaVCodePair]) -> List[Path]:
        self.output_dir.mkdir(parents=True, exist_ok=True)
        logger.info(f"Writing {len(pairs)} code pairs to {self.output_dir}")

        for pair in tqdm(pairs, desc="Writing MegaVul pairs"):
            pair_dir = self.write_single_pair(pair)
            self.pair_dirs.append(pair_dir)

        logger.info(f"Completed! Written {len(self.pair_dirs)} pairs to {self.output_dir}")
        return self.pair_dirs

    def write_single_pair(self, pair: MegaVCodePair) -> Path:
        # Coming format: <output>/<diff_folder>/<modif_file>/<diff_folder>_<modif_file>_{s,t}.java
        diff_folder = pair.commit_hash
        modif_file = pair.func_name

        func_dir = self.output_dir / diff_folder / modif_file
        func_dir.mkdir(parents=True, exist_ok=True)

        prefix = f"{diff_folder}_{modif_file}"
        with open(func_dir / f"{prefix}_s.java", "w", encoding="utf-8") as f:
            f.write(pair.vulnerable_code)

        with open(func_dir / f"{prefix}_t.java", "w", encoding="utf-8") as f:
            f.write(pair.patched_code)

        with open(func_dir / "metadata.json", "w", encoding="utf-8") as f:
            json.dump({
                "id": pair.id,
                "func_name": pair.func_name,
                "cve_id": pair.cve_id,
                "cwe_ids": pair.cwe_ids,
                "commit_hash": pair.commit_hash,
                "parent_commit_hash": pair.parent_commit_hash,
                "repo_name": pair.repo_name,
                "git_url": pair.git_url,
                "file_path": pair.file_path,
            }, f, indent=4)

        return func_dir
