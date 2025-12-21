import json
import logging
import subprocess
from pathlib import Path
import shutil
from typing import List

import pandas as pd
from tqdm import tqdm

from primevul_analysis.types import ComingChangeFrequency, ComingRunResult

logger = logging.getLogger(__name__)


def _truncate(text: str, limit: int = 2000) -> str:
    if len(text) <= limit:
        return text
    return text[:limit] + f"... (truncated {len(text) - limit} chars)"


class ComingTool:
    def __init__(self, coming_jar_path: Path = Path("/opt/coming.jar"), timeout: int = 300, xmx: str = "2G") -> None:
        """Wrapper for the Coming tool to extract AST-based change features."""
        self.coming_jar_path = Path(coming_jar_path)
        self.timeout = timeout
        self.xmx = xmx

        self._coming_cli = shutil.which("coming")

        if not self._coming_cli:
            raise FileNotFoundError("Coming CLI not found on PATH (expected `coming` executable)")

        logger.info(
            "Initialized ComingTool using CLI=%s timeout=%ss",
            self._coming_cli,
            self.timeout,
        )

    def analyze_pair(self, pair_dir: Path) -> ComingRunResult:
        """Run Coming analysis on a directory containing code pairs."""
        pair_dir = Path(pair_dir)
        vuln_path = pair_dir / "vulnerable.c"
        patched_path = pair_dir / "patched.c"

        # Validate directory
        if not pair_dir.exists() or not pair_dir.is_dir():
            logger.error("Pair directory %s does not exist or is not a directory", pair_dir)
            return ComingRunResult(
                pair_dir=pair_dir,
                success=False,
                error=f"Directory exists={pair_dir.exists()}, is_dir={pair_dir.is_dir()}"
            )

        # Validate vulnerable and patched files
        if not vuln_path.exists() or not patched_path.exists():
            logger.error("Missing vulnerable or patched file in %s", pair_dir)
            return ComingRunResult(
                pair_dir=pair_dir,
                success=False,
                error=(
                    "MissingFiles: "
                    f"vulnerable={vuln_path.exists()}, patched={patched_path.exists()}"
                )
            )

        # Run Coming tool via subprocess
        logger.info("Running Coming analysis on %s", pair_dir)
        cmd = [
            self._coming_cli,
            "-input",
            "filespair",
            "-location",
            f"{vuln_path}:{patched_path}",
            "-mode",
            "diff",
            "-parameters",
            "outputformat:json",
            "-output",
            str(pair_dir),
        ]

        logger.debug("Coming command: %s", " ".join(cmd))

        try:
            proc = subprocess.run(
                cmd,
                capture_output=True,
                text=True,
                timeout=self.timeout,
                check=False
            )
        except subprocess.TimeoutExpired as e:
            logger.warning("Coming analysis timed out for %s after %s seconds", pair_dir, self.timeout)
            return ComingRunResult(
                pair_dir=pair_dir,
                success=False,
                error=f"TimeoutExpired: Coming analysis timed out after {self.timeout} seconds",
                stdout=(e.stdout or ""), # TODO: May have to modify
                stderr=(e.stderr or "")
            )
        except Exception as e:
            logger.exception("Error running Coming analysis for %s", pair_dir)
            return ComingRunResult(
                pair_dir=pair_dir,
                success=False,
                error=f"InvocationError: {e}",
            )

        result = ComingRunResult(
            pair_dir=pair_dir,
            success=(proc.returncode == 0),
            error=(proc.stderr if proc.returncode != 0 else None),
            returncode=proc.returncode,
            stdout=proc.stdout or "",
            stderr=proc.stderr or "",
        )

        if not result.success:
            stderr_snippet = _truncate(result.stderr.strip())
            stdout_snippet = _truncate(result.stdout.strip())
            detail = stderr_snippet or stdout_snippet or "<no stdout/stderr captured>"
            logger.error(
                "Coming analysis failed for %s returncode=%s details=%s",
                pair_dir,
                proc.returncode,
                detail,
            )
            return result

        # Check if change_frequency.json exists after running Coming.
        freq_path = pair_dir / "change_frequency.json"
        if not freq_path.exists():
            logger.warning("change_frequency.json not found for %s", pair_dir)
            result.success = False
            result.error = "OutputMissing: change_frequency.json not found"
            return result

        # Fill in field in return type ComingRunResult
        result.change_frequency_path = freq_path

        # Validate and parse change_frequency.json
        try:
            with open(freq_path, "r", encoding="utf-8") as f:
                freq_data = json.load(f)
            result.change_frequency = ComingChangeFrequency.model_validate(freq_data)
        except Exception as e:
            logger.exception("Error parsing change_frequency.json for %s", pair_dir)
            result.success = False
            result.error = f"OutputParseError: {e}"

        return result

    def analyze_multiple_pairs(self, pairs_root: Path) -> List[ComingRunResult]:
        """Analyze multiple code pairs located in subdirectories of pairs_root."""
        pairs_root = Path(pairs_root)

        if not pairs_root.exists() or not pairs_root.is_dir():
            logger.error("Pairs root directory %s does not exist or is not a directory", pairs_root)
            return []

        pair_dirs = sorted([d for d in pairs_root.iterdir() if d.is_dir()])

        logger.info("Starting Coming analysis on %d code pairs in %s", len(pair_dirs), pairs_root)

        results: List[ComingRunResult] = []
        for pair_dir in tqdm(pair_dirs, desc="Analyzing code pairs with Coming"):
            result = self.analyze_pair(pair_dir)
            results.append(result)
        
        logger.info("Completed Coming analysis on all code pairs.")
        return results

    def summarize_results(self, results: List[ComingRunResult]) -> pd.DataFrame:
        """Summarize Coming analysis results into a DataFrame."""
        summary_data = []
        for res in results:
            entry = {
                "pair_dir": str(res.pair_dir),
                "success": res.success,
                "error": res.error,
                "returncode": res.returncode,
                "total_changes": res.total_changes() if res.change_frequency else None,
            }
            summary_data.append(entry)
        
        df = pd.DataFrame(summary_data)
        logger.info("Generated summary DataFrame for Coming analysis results.")
        return df
