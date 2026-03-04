"""
Ad-hoc script to run the 3 missing paper configs and write results
directly into data/results/paper/.

Missing configs:
  - mi50/configs/008_mi50_tabu100_indeg3
  - mi100_150_200/configs/007_mi100_tabu100_indegNone
  - mi100_150_200/configs/009_mi100_tabu100_indeg5

Usage
-----
    uv run python -m diff_analysis.scripts.megavul.bn1.run_missing_paper_configs
"""

import logging
from multiprocessing import Pool

import pandas as pd
import tqdm

from diff_analysis.scripts.megavul.bn1.tune_bn1 import run_config
from diff_analysis.utils.config_utils import find_project_root
from diff_analysis.utils.logging import setup_logging

setup_logging(level=logging.INFO)
logger = logging.getLogger(__name__)

if __name__ == "__main__":
    project_root = find_project_root()
    paper_dir = project_root / "data" / "results" / "paper"
    data_path = project_root / "data" / "processed" / "feature_matrix.parquet"

    logger.info(f"Loading feature matrix from {data_path}")
    df_raw = pd.read_parquet(data_path)

    missing = [
        (
            paper_dir / "mi50" / "configs" / "008_mi50_tabu100_indeg3",
            8,
            {"mi_threshold": 50, "tabu_length": 100, "max_indegree": 3},
        ),
        (
            paper_dir / "mi100" / "configs" / "007_mi100_tabu100_indegNone",
            7,
            {"mi_threshold": 100, "tabu_length": 100, "max_indegree": None},
        ),
        (
            paper_dir / "mi100" / "configs" / "009_mi100_tabu100_indeg5",
            9,
            {"mi_threshold": 100, "tabu_length": 100, "max_indegree": 5},
        ),
    ]

    work_items = []
    for cfg_dir, i, cfg in missing:
        cfg_dir.mkdir(parents=True, exist_ok=True)
        work_items.append((i, cfg, df_raw, cfg_dir, False))

    with Pool(processes=len(work_items)) as pool:
        for result in tqdm.tqdm(
            pool.imap_unordered(run_config, work_items),
            total=len(work_items),
            desc="configs",
        ):
            logger.info(
                f"Config {result['config_index']} done — "
                f"bic={result.get('bic_score')}, elapsed={result.get('elapsed_s')}s"
            )
