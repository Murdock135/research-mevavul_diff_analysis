"""
NOTE: This script MUST BE run inside a Docker container with Coming installed and configured, as it relies on Coming's command-line interface to compute diffs.
Usage:
  python analysis/megavul/03_get_diffs.py bug_fixing
  python analysis/megavul/03_get_diffs.py bug_inducing

bug_fixing:
Get diff results from coming tool by treating vulnerable code as source and patched code as target.

bug_inducing:
Get diff results from coming tool by treating patched code as source and vulnerable code as target.
"""

from megavul_diff_analysis.difftools.coming_tool import ComingTool
from megavul_diff_analysis.utils.config_utils import find_project_root

from pathlib import Path
from typing import List
import logging
import sys

from megavul_diff_analysis.utils.logging import setup_logging

from tqdm import tqdm

setup_logging(level=logging.ERROR)
logger = logging.getLogger(__name__)


def get_func_dirs(pairs_root: Path) -> List[Path]:
    """Walk two levels deep to collect all <commit_hash>/<func_name> directories.
    Note: This only works with the directory structure produced by `MegaVulDataPreparator.write_pairs`, where each commit hash directory contains subdirectories for each function.
    """
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

def diffs_bug_fixing(pairs_root: Path, coming_tool: ComingTool):
    func_dirs = get_func_dirs(pairs_root)

    succeeded = 0
    failed = 0
    for func_dir in tqdm(func_dirs, desc="Running Coming (bug_fixing)"):
        source_file, target_file = megavul_file_resolver(func_dir)
        result = coming_tool.analyze_pair(
            func_dir,
            source_file=source_file,
            target_file=target_file,
            output_suffix="bug_fixing",
        )
        if result.success:
            succeeded += 1
        else:
            failed += 1
            logger.error("Failed for %s: %s", func_dir, result.error)
    return succeeded, failed


def diffs_bug_inducing(pairs_root: Path, coming_tool: ComingTool):
    func_dirs = get_func_dirs(pairs_root)

    succeeded = 0
    failed = 0
    for func_dir in tqdm(func_dirs, desc="Running Coming (bug_inducing)"):
        source_file, target_file = megavul_file_resolver(func_dir)
        result = coming_tool.analyze_pair(
            func_dir,
            source_file=target_file,
            target_file=source_file,
            output_suffix="bug_inducing",
        )
        if result.success:
            succeeded += 1
        else:
            failed += 1
            logger.error("Failed for %s: %s", func_dir, result.error)
    return succeeded, failed

def main():
    if len(sys.argv) < 2 or sys.argv[1] not in ["bug_fixing", "bug_inducing"]:
        print(__doc__)
        sys.exit(1)

    project_root = find_project_root()

    pairs_root = project_root / "data" / "interim" / "megavul"

    coming_tool = ComingTool()

    if sys.argv[1] == "bug_fixing":
        succeeded, failed = diffs_bug_fixing(pairs_root, coming_tool)
    elif sys.argv[1] == "bug_inducing":
        succeeded, failed = diffs_bug_inducing(pairs_root, coming_tool)

    print(f"Coming analysis complete: {succeeded} succeeded, {failed} failed")


if __name__ == "__main__":
    main()
