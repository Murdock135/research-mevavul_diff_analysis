from pathlib import Path
import logging
import sys
import json

import pandas as pd

from diff_analysis.utils.config_utils import find_project_root
from diff_analysis.utils.logging import setup_logging
from diff_analysis.types import ComingChangeFrequency, ComingPairDataDir, FuncDir, HashDir

setup_logging(level=logging.DEBUG)
logger = logging.getLogger(__name__)

# Helper function just to check if all func dirs exist
def print_func_dirs(pairs_dir: Path):
    func_dir_count = 1
    for pair_dir in sorted(pairs_dir.iterdir()):
        for func_dir in sorted(pair_dir.iterdir()):
            print(f"{func_dir_count}. {pair_dir}/{func_dir}")
            func_dir_count += 1

def build_type(pairs_dir: Path) -> ComingPairDataDir:
    hash_dirs: list[HashDir] = []

    i = 1
    for hash_dir_path in sorted(pairs_dir.iterdir()):
        func_dirs_list: list[FuncDir] = []
        for func_dir_path in sorted(hash_dir_path.iterdir()):
            source, = func_dir_path.glob("*_s.java")
            target, = func_dir_path.glob("*_t.java")
            change_frequency_bug_fixing = func_dir_path / "change_frequency_bug_fixing.json"
            change_frequency_bug_inducing = func_dir_path / "change_frequency_bug_inducing.json"
            metadata = func_dir_path / "metadata.json"

            if not change_frequency_bug_fixing.exists() or not change_frequency_bug_inducing.exists():
                logger.debug("Could not find %s or %s in %s", str(change_frequency_bug_fixing), str(change_frequency_bug_inducing), str(hash_dir_path / func_dir_path))

            if not metadata.exists():
                logger.debug("Could not find %s in %s", str(metadata), str(hash_dir_path / func_dir_path))

            func_dir = FuncDir(
                source=source,
                target=target,
                change_frequency_bug_fixing=change_frequency_bug_fixing,
                change_frequency_bug_inducing=change_frequency_bug_inducing,
                metadata=metadata
            )
            func_dirs_list.append(func_dir)
            i += 1

            logger.info("Processed func dir %d: %s", i, str(func_dir_path))

        hash_dir = HashDir(func_dirs=func_dirs_list)
        hash_dirs.append(hash_dir)

    return ComingPairDataDir(HashDirs=hash_dirs)


class FeatureMatrixBuilder:
    def __init__(self, coming_data_dir: ComingPairDataDir):
        self.coming_data_dir = coming_data_dir

    def get_frequency_parents(self, change_frequency_file: Path) -> dict[str, int] | None:
        """Return {change_type: count} from a change_frequency_*.json file, or None on error."""
        if not change_frequency_file.exists():
            logger.warning("Change frequency file not found: %s", change_frequency_file)
            return None

        try:
            change_frequency = ComingChangeFrequency.model_validate_json(change_frequency_file.read_text())
        except Exception as e:
            logger.warning("Failed to parse %s: %s", change_frequency_file, e)
            return None

        return {entry.c: entry.f for entry in change_frequency.frequency_parent}

    def _load_metadata(self, metadata_file: Path) -> dict | None:
        if not metadata_file.exists():
            logger.warning("Metadata file not found: %s", metadata_file)
            return None
        try:
            with open(metadata_file, "r") as f:
                return json.load(f)
        except json.JSONDecodeError as e:
            logger.warning("Failed to decode JSON from %s: %s", metadata_file, e)
            return None

    def build(self) -> pd.DataFrame:
        """
        Build a feature matrix from the coming data directory. The feature matrix includes features from
        change_frequency_*.json files and metadata.json files. Each row corresponds to a function directory,
        with features indicating the presence of change types and metadata information.
        Returns:
            pd.DataFrame: A DataFrame containing the combined features and metadata.

        How the feature matrix is built:
        - For each function directory in the coming data directory:
            - Load metadata from metadata.json
            - Load change frequency data from change_frequency_bug_fixing.json and change_frequency_bug_inducing.json
            - Create feature rows indicating the presence of change types
            - Create metadata rows including the "is_vul" label and other metadata
        - Combine feature rows and metadata rows into a single DataFrame
        - Return the resulting DataFrame
        """
        feature_rows: list[dict] = []       # features from change_frequency_*.json
        meta_rows: list[dict] = []          # features from metadata.json

        for hash_dir in self.coming_data_dir.HashDirs:
            for func_dir in hash_dir.func_dirs:

                # ================ METADATA FEATURES =============================
                metadata = self._load_metadata(func_dir.metadata)
                if metadata is None:
                    continue

                # ================ CHANGE FREQUENCY FEATURES =====================
                bug_fixing: dict[str, int] | None = self.get_frequency_parents(func_dir.change_frequency_bug_fixing)
                bug_inducing: dict[str, int] | None = self.get_frequency_parents(func_dir.change_frequency_bug_inducing)

                for freq, is_vul in [(bug_fixing, False), (bug_inducing, True)]:
                    if freq is None:
                        continue
                    feature_rows.append({col: int(f > 0) for col, f in freq.items()})
                    meta_rows.append({"is_vul": is_vul} | metadata)

        feature_df = pd.DataFrame(feature_rows).fillna(0).astype(int)
        meta_df = pd.DataFrame(meta_rows)
        df = pd.concat([feature_df, meta_df], axis=1)

        logger.info("Feature matrix shape: %d rows x %d columns", *df.shape)
        return df


if __name__ == "__main__":
    project_root = find_project_root()
    megavul_pairs_path = project_root / 'data' / 'interim' / 'megavul_pairs'

    if not megavul_pairs_path.exists():
        logger.error("Path %s does not exist", str(megavul_pairs_path))
        sys.exit(1)

    coming_data_dir: ComingPairDataDir = build_type(megavul_pairs_path)
    builder = FeatureMatrixBuilder(coming_data_dir)
    df = builder.build()

    output_path = project_root / "data" / "processed" / "feature_matrix.parquet"
    df.to_parquet(output_path, index=False)
    logger.info("Saved feature matrix to %s", output_path)
