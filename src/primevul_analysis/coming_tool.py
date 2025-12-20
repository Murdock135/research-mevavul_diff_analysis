import json
import logging
import subprocess
from pathlib import Path
from typing import List, Optional

import pandas as pd
from tqdm import tqdm

from primevul_analysis.types import ComingChangeFrequency, ComingRunResult

logger = logging.getLogger(__name__)


class ComingTool:
    def __init__(self, coming_jar_path: Path = Path("/opt/coming.jar"), timeout: int = 300, xmx: str = "2G") -> None:
        """Wrapper for the Coming tool to extract AST-based change features."""
        self.coming_jar_path = coming_jar_path
        self.timeout = timeout
        self.xmx = xmx

        if not self.coming_jar_path.exists():
            raise FileNotFoundError(f"Coming jar not found at {self.coming_jar_path}")
        
        logger.info(f"Initialized ComingTool with jar at {self.coming_jar_path}, timeout {self.timeout}s, and Xmx {self.xmx}")

    def analyze_pair_dir(self, pair_dir: Path) -> ComingRunResult:
        """Run Coming analysis on a directory containing code pairs."""
        pair_dir = Path(pair_dir)
        vuln_path = pair_dir / "vulnerable.c"
        patched_path = pair_dir / "patched.c"

        # Run Coming tool via subprocess
        logger.info(f"Running Coming analysis on {pair_dir}")
        cmd = [
            "java",
            "-Xmx" + self.xmx,
            "-jar", str(self.coming_jar_path),
            "-input", "filespair",
            "-location", f"{vuln_path}:{patched_path}",
            "-mode", "diff",
            "-parameters", "outputformat:json",
            "-output", str(pair_dir)
        ]

        try:
            proc = subprocess.run(
                cmd,
                capture_output=True,
                text=True,
                timeout=self.timeout,
                check=False
            )
        except subprocess.TimeoutExpired as e:
            logger.error(f"Coming analysis timed out for {pair_dir} after {self.timeout} seconds")
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
            logger.error(
                "Coming analysis failed for %s returncode=%s", pair_dir, proc.returncode
            )
            return result

        # Coming commonly writes this in the output directory.
        freq_path = pair_dir / "change_frequency.json"
        if not freq_path.exists():
            logger.warning("change_frequency.json not found for %s", pair_dir)
            result.success = False
            result.error = "OutputMissing: change_frequency.json not found"
            return result

        result.change_frequency_path = freq_path

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
        pair_dirs = [d for d in pairs_root.iterdir() if d.is_dir()]

        logger.info(f"Starting Coming analysis on {len(pair_dirs)} code pairs in {pairs_root}")

        results: List[ComingRunResult] = []
        for pair_dir in tqdm(pair_dirs, desc="Analyzing code pairs with Coming"):
            result = self.analyze_pair_dir(pair_dir)
            
            if result:
                results.append(result)
        
        logger.info("Completed Coming analysis on all code pairs.")
        return results

    def summarize_results(self, results: List[ComingRunResult]) -> pd.DataFrame:
        """Summarize Coming analysis results into a DataFrame."""
        summary_data = []
        for res in results:
            entry = {
                "pair_dir": res.pair_dir,
                "success": res.success,
                "error": res.error,
                "returncode": res.returncode,
                "total_changes": res.total_changes() if res.change_frequency else None,
            }
            summary_data.append(entry)
        
        df = pd.DataFrame(summary_data)
        logger.info("Generated summary DataFrame for Coming analysis results.")
        return df
