"""
ExperimentTracker — manages directory layout, naming, and incremental saves
for a BN grid-search run.

Directory layout
----------------
<base_dir>/<timestamp>/
    experiment.json              written once upfront before any config runs
    configs/
        <NNN_mi{m}_tabu{t}_indeg{d}>/
            config.json          hyperparams + started_at
            result.json          outcome + ended_at
            hcs_restarts.jsonl   one line per HCS restart (score, f1, cn, edges)
    summary.csv                  appended after each config completes
    summary.jsonl                same, JSONL format

Typical usage
-------------
    from diff_analysis.scripts.megavul.experiment_tracker import ExperimentTracker

    tracker = ExperimentTracker(base_dir, experiment_config)

    for i, cfg in enumerate(configs, 1):
        cfg_dir = tracker.config_dir(i, **cfg)
        tracker.save_config_start(cfg_dir, {**cfg, "started_at": ...})
        # ... run pipeline ...
        tracker.save_hcs_restarts(cfg_dir, pipeline.hcs_history)
        tracker.save_config_result(cfg_dir, result)

    tracker.finalize()
"""

import json
import logging
from datetime import datetime
from pathlib import Path

import pandas as pd

logger = logging.getLogger(__name__)


class ExperimentTracker:
    def __init__(self, base_dir: Path, experiment_config: dict) -> None:
        ts = datetime.now().strftime("%Y%m%d_%H%M%S")
        self.run_dir      = base_dir / ts
        self.configs_dir  = self.run_dir / "configs"
        self.configs_dir.mkdir(parents=True, exist_ok=True)
        self.summary_csv   = self.run_dir / "summary.csv"
        self.summary_jsonl = self.run_dir / "summary.jsonl"

        started_at = datetime.now().isoformat(timespec="seconds")
        with open(self.run_dir / "experiment.json", "w") as f:
            json.dump({"started_at": started_at, **experiment_config}, f, indent=2, default=str)
        logger.info(f"Experiment run → {self.run_dir}")

    def config_dir(self, index: int, mi: int, tabu: int, indeg: int | None) -> Path:
        indeg_tag = str(indeg) if indeg is not None else "None"
        d = self.configs_dir / f"{index:03d}_mi{mi}_tabu{tabu}_indeg{indeg_tag}"
        d.mkdir(parents=True, exist_ok=True)
        return d

    def save_config_start(self, cfg_dir: Path, hyperparams: dict) -> None:
        with open(cfg_dir / "config.json", "w") as f:
            json.dump(hyperparams, f, indent=2, default=str)

    def save_hcs_restarts(self, cfg_dir: Path, hcs_history: list[dict]) -> None:
        with open(cfg_dir / "hcs_restarts.jsonl", "w") as f:
            for entry in hcs_history:
                f.write(json.dumps(entry) + "\n")

    def save_config_result(self, cfg_dir: Path, result: dict) -> None:
        with open(cfg_dir / "result.json", "w") as f:
            json.dump(result, f, indent=2, default=str)
        pd.DataFrame([result]).to_csv(
            self.summary_csv, mode="a", header=not self.summary_csv.exists(), index=False,
        )
        with open(self.summary_jsonl, "a") as f:
            f.write(json.dumps(result, default=str) + "\n")

    def finalize(self) -> None:
        """Re-sort summary CSV by bic_score descending and log the best config."""
        if not self.summary_csv.exists():
            logger.warning("No configs completed — nothing to finalize.")
            return
        df = (
            pd.read_csv(self.summary_csv)
            .sort_values("bic_score", ascending=False)
            .reset_index(drop=True)
        )
        df.to_csv(self.summary_csv, index=False)
        logger.info(f"Final ranked results → {self.summary_csv}")
        valid = df.dropna(subset=["bic_score"])
        if not valid.empty:
            best = valid.iloc[0]
            logger.info(
                f"Best config: mi_threshold={best.mi_threshold}, "
                f"tabu_length={best.tabu_length}, max_indegree={best.max_indegree} "
                f"→ BIC={best.bic_score}, edges={best.n_edges}"
            )
