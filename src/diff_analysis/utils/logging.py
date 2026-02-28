import logging
from pathlib import Path

from diff_analysis.utils.config_utils import find_project_root


def setup_logging(level: int = logging.DEBUG) -> None:
    """Configure logging for all diff_analysis scripts.

    Writes to logs/diff_analysis.log under the project root and to stdout.
    The log directory is created if it does not exist.
    """
    log_dir = find_project_root() / "logs"
    log_dir.mkdir(exist_ok=True)

    logging.basicConfig(
        level=level,
        format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
        handlers=[
            logging.FileHandler(log_dir / "diff_analysis.log"),
            logging.StreamHandler(),
        ],
    )
