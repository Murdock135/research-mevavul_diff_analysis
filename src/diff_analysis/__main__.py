"""
Entry point for `python -m diff_analysis`.

Usage:
  python -m diff_analysis primevul   # run the PrimeVul pipeline (steps 1-3)
  python -m diff_analysis megavul    # run the MegaVul pipeline (steps 1-3)
"""

import sys


def main():
    if len(sys.argv) < 2 or sys.argv[1] not in ("primevul", "megavul"):
        print(__doc__)
        sys.exit(1)

    if sys.argv[1] == "primevul":
        from pipeline_primevul import main as run
    else:
        from pipeline_megavul import main as run

    run()


if __name__ == "__main__":
    main()
