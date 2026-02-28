"""
Entry point for `python -m diff_analysis`.

Usage:
  python -m diff_analysis primevul                    # run the PrimeVul pipeline (steps 1-3)
  python -m diff_analysis megavul bug_fixing          # run the MegaVul pipeline (bug_fixing direction)
  python -m diff_analysis megavul bug_inducing        # run the MegaVul pipeline (bug_inducing direction)
"""

import sys

from diff_analysis.utils.config_utils import find_project_root


def main():
    if len(sys.argv) < 2 or sys.argv[1] not in ("primevul", "megavul"):
        print(__doc__)
        sys.exit(1)

    # pipeline_*.py live at the project root, which is not on sys.path when
    # running as `python -m diff_analysis`. Add it so the imports resolve.
    project_root = str(find_project_root())
    if project_root not in sys.path:
        sys.path.insert(0, project_root)

    if sys.argv[1] == "primevul":
        from pipeline_primevul import main as run
    else:
        from pipeline_megavul import main as run

    # Strip the dataset arg so the pipeline script sees its own args at sys.argv[1:].
    # e.g. ["...", "megavul", "bug_fixing"] → ["...", "bug_fixing"]
    sys.argv = [sys.argv[0]] + sys.argv[2:]
    run()


if __name__ == "__main__":
    main()
