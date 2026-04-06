"""
Entry point for `python -m diff_analysis`.

Usage:
  python -m diff_analysis megavul bug_fixing    # run the MegaVul pipeline (bug_fixing direction)
  python -m diff_analysis megavul bug_inducing  # run the MegaVul pipeline (bug_inducing direction)
"""

import sys

from megavul_diff_analysis.utils.config_utils import find_project_root


def main():
    project_root = str(find_project_root())
    if project_root not in sys.path:
        sys.path.insert(0, project_root)

    from pipeline_megavul import main as run

    sys.argv = [sys.argv[0]] + sys.argv[2:]
    run()


if __name__ == "__main__":
    main()
