from diff_analysis.difftools.coming_tool import ComingTool
from diff_analysis.utils.config_utils import find_project_root

from pathlib import Path
from typing import List
import logging

from diff_analysis.utils.logging import setup_logging

from tqdm import tqdm

setup_logging(level=logging.ERROR)
logger = logging.getLogger(__name__)


def get_func_dirs(pairs_root: Path) -> List[Path]:
    """Walk two levels deep to collect all <commit_hash>/<func_name> directories."""
    func_dirs = []
    for commit_dir in sorted(pairs_root.iterdir()):
        if commit_dir.is_dir():
            for func_dir in sorted(commit_dir.iterdir()):
                if func_dir.is_dir():
                    func_dirs.append(func_dir)
    return func_dirs


def megavul_file_resolver(func_dir: Path):
    """Return the _s.java and _t.java filenames for a given func_dir."""
    commit_hash = func_dir.parent.name
    func_name = func_dir.name
    prefix = f"{commit_hash}_{func_name}"
    return f"{prefix}_s.java", f"{prefix}_t.java"


def main():
    project_root = find_project_root()

    pairs_root = project_root / "data" / "interim" / "megavul_pairs" / "bug_fixing"
    func_dirs = get_func_dirs(pairs_root)

    coming_tool = ComingTool()

    succeeded = 0
    failed = 0
    for func_dir in tqdm(func_dirs, desc="Running Coming"):
        source_file, target_file = megavul_file_resolver(func_dir)
        result = coming_tool.analyze_pair(func_dir, source_file=source_file, target_file=target_file)
        if result.success:
            succeeded += 1
        else:
            failed += 1
            logger.error("Failed for %s: %s", func_dir, result.error)

    print(f"Coming analysis complete: {succeeded} succeeded, {failed} failed")


if __name__ == "__main__":
    main()
