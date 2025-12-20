import logging
import subprocess
import json
from pathlib import Path
from typing import List, Dict, Any
import pandas as pd
from tqdm import tqdm

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

    def analyze_pair_dir(self, pair_dir: Path):
        """Run Coming analysis on a directory containing code pairs."""
        output_path = pair_dir / "coming_output.json"
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
            result = subprocess.run(
                cmd,
                capture_output=True,
                text=True,
                timeout=self.timeout,
                check=False
            )
        except subprocess.TimeoutExpired:
            logger.error(f"Coming analysis timed out for {pair_dir} after {self.timeout} seconds")
            return None
            # if result.returncode != 0:
            #     logger.error(f"Coming analysis failed for {pair_dir} with error: {result.stderr}")
            #     return 


        