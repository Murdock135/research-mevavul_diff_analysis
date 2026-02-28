from pathlib import Path
from typing import Optional, Literal
from concurrent.futures import ThreadPoolExecutor, as_completed
import subprocess
import logging
import shutil

from diff_analysis.types import GumTreeDiffResult
from diff_analysis.utils.str_utils import _truncate

from tqdm import tqdm

logger = logging.getLogger(__name__)


class GumTreeTool:
    """
    Wrapper for GumTree tool to extract AST-based diffs between code files.
    """
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
                diff_path=None
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
            logger.debug("No output_path specified for %s and %s; diff will be in stdout", file1, file2)

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
                diff_path=None,
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
                diff_path=None,
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
            diff_path=output_path,
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

    def batch_text_diff(
        self,
        file_pairs: list[tuple[Path, Path]],
        output_dir: Path,
        output_format: Literal["XML", "JSON", "TEXT"] = "XML",
    ) -> list[GumTreeDiffResult]:
        """Batch process multiple file pairs for text diffs using GumTree."""

        # Ensure output directory exists
        output_dir.mkdir(parents=True, exist_ok=True)

        results = []
        for idx, (file1, file2) in enumerate(tqdm(file_pairs, desc="GumTree Text Diffs")):
            # Create unique output path using index to avoid collisions
            output_filename = f"{idx}_{file1.stem}_to_{file2.stem}_diff.{output_format.lower()}"
            output_path = output_dir / output_filename

            result = self.get_text_diff(
                file1=file1,
                file2=file2,
                output_path=output_path,
                output_format=output_format,
            )
            results.append(result)

        return results


class GumTreeToolPairsExecutor:
    """
    Wrapper for GumTree tool to extract AST-based diffs between code files in pair directories.

    Structure of each pair directory is assumed to be:
        pair_dir/
            original.c
            modified.c
    """
    def __init__(
        self,
        gumtree_tool: GumTreeTool,
    ) -> None:
        self.gumtree_tool = gumtree_tool

    def analyze_pair_dir(
        self,
        pair_dir: Path,
        file1_stem: str = "original",
        file2_stem: str = "modified",
        suffix: Literal[".c", ".cpp", ".java", ".py"] = ".c",
        output_dir: Optional[Path] = None,
        output_format: Literal["XML", "JSON", "TEXT"] = "XML",
        idx: Optional[int] = None,
    ) -> GumTreeDiffResult:
        """Analyze a single pair directory for GumTree text diff."""

        # Validate directory
        pair_dir = Path(pair_dir)
        if not pair_dir.exists() or not pair_dir.is_dir():
            error_msg = "Pair directory %s does not exist or is not a directory" % pair_dir
            logger.error(error_msg)
            return GumTreeDiffResult(
                file1=pair_dir,
                file2=pair_dir,
                diff_output="",
                error=error_msg,
            )

        file1 = pair_dir / f"{file1_stem}{suffix}"
        file2 = pair_dir / f"{file2_stem}{suffix}"

        # Validate files
        if not file1.exists() or not file2.exists():
            error_msg = "Missing files in %s: %s exists=%s, %s exists=%s" % (
                pair_dir,
                file1,
                file1.exists(),
                file2,
                file2.exists(),
            )
            logger.error(error_msg)
            return GumTreeDiffResult(
                file1=file1,
                file2=file2,
                diff_output="",
                error=error_msg,
                diff_path=None,
            )

        # Build output path
        target_dir = output_dir if output_dir else pair_dir
        prefix = f"{idx}_" if idx is not None else ""
        output_path = target_dir / f"{prefix}{file1_stem}_to_{file2_stem}.{output_format.lower()}"

        result = self.gumtree_tool.get_text_diff(
            file1, 
            file2, 
            output_path=output_path,
            output_format=output_format
        )

        return result

    def batch_analyze_pair_dirs(
        self,
        pairs_dirs: list[Path],
        file1_stem: str = "original",
        file2_stem: str = "modified",
        suffix: Literal[".c", ".cpp", ".java", ".py"] = ".c",
        output_dir: Optional[Path] = None,
        output_format: Literal["XML", "JSON", "TEXT"] = "XML",
        max_workers: int = 4,
    ) -> list[GumTreeDiffResult]:
        """Batch process multiple pair directories for GumTree text diffs.
        
        Args:
            pairs_dirs: List of directories each containing a pair of code files.
            file1_stem: Stem for the first file in each pair.
            file2_stem: Stem for the second file in each pair.
            suffix: Suffix/extension of the code files.
            output_dir: Directory to save output diff files. If None, saves in each pair directory.
            output_format: Format of the GumTree diff output.
            max_workers: Number of parallel workers for processing.
        """

        # Sort pair directories for consistent processing order
        pairs_dirs = sorted(pairs_dirs)

        results = []
        with ThreadPoolExecutor(max_workers=max_workers) as executor:
            future_to_data = {
                executor.submit(
                    self.analyze_pair_dir,
                    pair_dir,
                    file1_stem,
                    file2_stem,
                    suffix,
                    output_dir,
                    output_format,
                    idx,
                ): (idx, pair_dir)
                for idx, pair_dir in enumerate(pairs_dirs)
            }

            for future in tqdm(
                as_completed(future_to_data),
                total=len(future_to_data),
                desc="GumTree Pair Diffs",
            ):
                idx, pair_dir = future_to_data[future]
                try:
                    result = future.result()
                    results.append(result)
                except Exception as e:
                    error_msg = "Error processing pair directory %s: %s" % (pair_dir, e)
                    logger.exception(error_msg)
                    results.append(
                        GumTreeDiffResult(
                            file1=pair_dir,
                            file2=pair_dir,
                            diff_output="",
                            error=error_msg,
                        )
                    )

        return results