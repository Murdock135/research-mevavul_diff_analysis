from pathlib import Path
from typing import Optional, Literal
import subprocess
import logging
import shutil

from primevul_analysis.types import GumTreeDiffResult
from primevul_analysis.utils import _truncate

logger = logging.getLogger(__name__)


class GumTreeTool:
    def __init__(
        self, gumtree_path: Path = Path("/usr/local/bin/gumtree"), timeout: int = 60
    ) -> None:
        self.gumtree_path = gumtree_path
        self.timeout = timeout

        if not shutil.which(str(self.gumtree_path)):
            raise FileNotFoundError(
                f"GumTree executable not found at {self.gumtree_path}"
            )

        logger.info("Initialized GumTreeTool using path=%s", self.gumtree_path)

    def get_text_diff(
        self,
        file1: Path,
        file2: Path,
        output_path: Optional[Path] = None,
        output_format: Literal["XML", "JSON", "TEXT"] = "XML",
    ) -> GumTreeDiffResult:
        """Get the textual diff between two files using GumTree."""

        # Validate input files
        if not file1.exists() or not file2.exists():
            error_msg = "file1 exists=%s, file2 exists=%s" % (
                file1.exists(),
                file2.exists(),
            )
            logger.error(error_msg)
            return GumTreeDiffResult(
                file1=file1,
                file2=file2,
                diff_output="",
                error=error_msg,
            )

        # Build command
        cmd = [
            str(self.gumtree_path),
            "textdiff",
            str(file1),
            str(file2),
            "-f",
            output_format,
        ]

        if output_path:
            # Ensure output directory exists
            output_path.parent.mkdir(parents=True, exist_ok=True)
            cmd.extend(["-o", str(output_path)])
        else:
            logger.warning("No output_path specified for %s and %s; diff will be in stdout", file1, file2)

        logger.debug("Running GumTree command: %s", " ".join(cmd))

        try:
            proc = subprocess.run(
                cmd,
                capture_output=True,
                text=True,
                timeout=self.timeout,
            )
        except subprocess.TimeoutExpired as e:
            error_msg = "GumTree textdiff timed out after %s seconds for %s and %s" % (
                self.timeout,
                file1,
                file2,
            )
            logger.warning(error_msg)
            return GumTreeDiffResult(
                file1=file1,
                file2=file2,
                diff_output="",
                error=error_msg,
                stderr=str(
                    e.stderr.decode()
                    if isinstance(e.stderr, bytes)
                    else (e.stderr or "")
                ),
                stdout=str(
                    e.stdout.decode()
                    if isinstance(e.stdout, bytes)
                    else (e.stdout or "")
                ),
            )
        except Exception as e:
            error_msg = "Error running GumTree textdiff for %s and %s: %s" % (
                file1,
                file2,
                e,
            )
            logger.exception(error_msg)
            return GumTreeDiffResult(
                file1=file1,
                file2=file2,
                diff_output="",
                error=error_msg,
            )

        # Get diff output - read from file if specified, otherwise from stdout
        diff_output = ""
        if output_path and output_path.exists():
            try:
                diff_output = output_path.read_text()
            except Exception as e:
                logger.error("Failed to read output file %s: %s", output_path, e)
                diff_output = proc.stdout
        else:
            diff_output = proc.stdout

        result = GumTreeDiffResult(
            file1=file1,
            file2=file2,
            diff_output=diff_output,
            returncode=proc.returncode,
            stderr=proc.stderr,
            stdout=proc.stdout,
        )

        # GumTree can return non-zero when files differ (expected behavior)
        # Only log error if there's actual stderr content
        if result.returncode != 0 and result.stderr:
            logger.warning(
                "GumTree textdiff returned non-zero for %s and %s (returncode=%s, stderr=%s)",
                file1,
                file2,
                result.returncode,
                _truncate(result.stderr),
            )

        return result
