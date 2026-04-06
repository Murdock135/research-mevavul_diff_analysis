import json
import logging
import subprocess
from pathlib import Path
import shutil
from typing import Callable, List, Optional, Tuple

import pandas as pd
from tqdm import tqdm

from megavul_diff_analysis.types import ComingChangeFrequency, ComingRunResult
from megavul_diff_analysis.utils.str_utils import _truncate

logger = logging.getLogger(__name__)

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

    def analyze_pair(
        self,
        pair_dir: Path,
        source_file: str = "vulnerable.c",
        target_file: str = "patched.c",
        output_suffix: Optional[str] = None,
    ) -> ComingRunResult:
        """Run Coming analysis on a directory containing code pairs.

        Args:
            pair_dir: Directory containing the source and target files.
            source_file: Filename of the vulnerable/before file within pair_dir.
            target_file: Filename of the patched/after file within pair_dir.
            output_suffix: If given, rename change_frequency.json to
                change_frequency_{output_suffix}.json after a successful run.
                Use this to keep bug_fixing and bug_inducing outputs in the
                same directory without overwriting each other.
        """
        pair_dir = Path(pair_dir)
        vuln_path = pair_dir / source_file
        patched_path = pair_dir / target_file

        # Validate directory
        if not pair_dir.exists() or not pair_dir.is_dir():
            logger.error("Pair directory %s does not exist or is not a directory", pair_dir)
            return ComingRunResult(
                pair_dir=pair_dir,
                success=False,
                error=f"Directory exists={pair_dir.exists()}, is_dir={pair_dir.is_dir()}"
            )

        # Validate source and target files
        if not vuln_path.exists() or not patched_path.exists():
            logger.error("Missing source or target file in %s", pair_dir)
            return ComingRunResult(
                pair_dir=pair_dir,
                success=False,
                error=(
                    "MissingFiles: "
                    f"source={vuln_path.exists()}, target={patched_path.exists()}"
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

        # Rename output file if a suffix was requested (e.g. "bug_fixing" →
        # change_frequency_bug_fixing.json) so that repeated runs in different
        # directions don't overwrite each other.
        if output_suffix:
            dest = pair_dir / f"change_frequency_{output_suffix}.json"
            freq_path.rename(dest)
            freq_path = dest

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

    def analyze_multiple_pairs(
        self,
        pairs_root: Path,
        file_resolver: Optional[Callable[[Path], Tuple[str, str]]] = None,
    ) -> List[ComingRunResult]:
        """Analyze multiple code pairs located in subdirectories of pairs_root.

        Args:
            pairs_root: Root directory containing one subdirectory per pair.
            file_resolver: Optional callable that takes a pair directory and returns
                (source_filename, target_filename). Defaults to ("vulnerable.c", "patched.c").
        """
        pairs_root = Path(pairs_root)

        if not pairs_root.exists() or not pairs_root.is_dir():
            logger.error("Pairs root directory %s does not exist or is not a directory", pairs_root)
            return []

        pair_dirs = sorted([d for d in pairs_root.iterdir() if d.is_dir()])

        logger.info("Starting Coming analysis on %d code pairs in %s", len(pair_dirs), pairs_root)

        results: List[ComingRunResult] = []
        for pair_dir in tqdm(pair_dirs, desc="Analyzing code pairs with Coming"):
            if file_resolver is not None:
                source_file, target_file = file_resolver(pair_dir)
                result = self.analyze_pair(pair_dir, source_file=source_file, target_file=target_file)
            else:
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
