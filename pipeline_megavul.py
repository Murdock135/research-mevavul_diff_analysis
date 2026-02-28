"""
MegaVul analysis pipeline — steps 1–3 (requires Docker / Coming tool).

Steps:
  1. Extract code pairs from MegaVul JSON → data/processed/megavul_pairs.csv
  2. Write pair directories for Coming → data/interim/megavul_pairs/
  3. Run Coming on all pairs → data/interim/megavul_pairs/<commit>/<func>/change_frequency.json

Run individually:
  uv run python -m diff_analysis.scripts.megavul.extract
  uv run python -m diff_analysis.scripts.megavul.create_pairs
  uv run python -m diff_analysis.scripts.megavul.get_diffs
"""

from diff_analysis.datapreparator.megavul import MegaVulExtractor, MegaVulDataPreparator
from diff_analysis.difftools.coming_tool import ComingTool
from diff_analysis.types import MegaVCodePair
from diff_analysis.utils.config_utils import find_project_root

from pathlib import Path
from typing import List
import logging

from diff_analysis.utils.logging import setup_logging

from tqdm import tqdm

setup_logging(level=logging.ERROR)
logger = logging.getLogger(__name__)


def extract_code_pairs(input_path: Path, output_path: Path) -> List[MegaVCodePair]:
    extractor = MegaVulExtractor(input_path=input_path, output_path=output_path)
    code_pairs: List[MegaVCodePair] = extractor.extract_code_pairs()
    extractor.save_code_pairs(code_pairs, format='csv', output_path=output_path / "megavul_pairs.csv")
    return code_pairs


def prepare_pairs(code_pairs: List[MegaVCodePair], output_dir: Path) -> None:
    preparator = MegaVulDataPreparator(output_dir=output_dir)
    preparator.write_pairs(code_pairs)


def run_coming_diffs(pairs_root: Path) -> None:
    def get_func_dirs(root: Path) -> List[Path]:
        func_dirs = []
        for commit_dir in sorted(root.iterdir()):
            if commit_dir.is_dir():
                for func_dir in sorted(commit_dir.iterdir()):
                    if func_dir.is_dir():
                        func_dirs.append(func_dir)
        return func_dirs

    def file_resolver(func_dir: Path):
        commit_hash = func_dir.parent.name
        func_name = func_dir.name
        prefix = f"{commit_hash}_{func_name}"
        return f"{prefix}_s.java", f"{prefix}_t.java"

    coming_tool = ComingTool()
    func_dirs = get_func_dirs(pairs_root)
    succeeded = failed = 0
    for func_dir in tqdm(func_dirs, desc="Running Coming"):
        source_file, target_file = file_resolver(func_dir)
        result = coming_tool.analyze_pair(func_dir, source_file=source_file, target_file=target_file)
        if result.success:
            succeeded += 1
        else:
            failed += 1
            logger.error("Failed for %s: %s", func_dir, result.error)
    print(f"Coming analysis complete: {succeeded} succeeded, {failed} failed")


def main():
    project_root = find_project_root()

    input_path = project_root / "data" / "raw" / "megavul" / "cve_with_graph_abstract_commit.json"
    processed_dir = project_root / "data" / "processed"
    interim_dir = project_root / "data" / "interim" / "megavul_pairs"

    processed_dir.mkdir(parents=True, exist_ok=True)

    print("Step 1: Extracting code pairs")
    code_pairs = extract_code_pairs(input_path=input_path, output_path=processed_dir)

    print("Step 2: Writing pair directories")
    prepare_pairs(code_pairs=code_pairs, output_dir=interim_dir)

    print("Step 3: Running Coming diffs")
    run_coming_diffs(pairs_root=interim_dir)

    print("Pipeline complete.")


if __name__ == "__main__":
    main()
