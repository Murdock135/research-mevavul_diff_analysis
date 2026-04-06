from pathlib import Path
from typing import List, Optional

from pydantic import BaseModel, ConfigDict, Field


class MegaVCodePair(BaseModel):
    id: str                          # unique identifier: f"{commit_hash}_{func_name}_{index}"
    func_name: str
    vulnerable_code: str             # func_before
    patched_code: str                # func_after
    cve_id: str
    cwe_ids: list[str]
    commit_hash: str
    parent_commit_hash: str
    repo_name: str
    git_url: str
    file_path: str                   # relative path within repo
    file_name: str                   # basename of the file containing the function
    commit_date: int                 # Unix timestamp of the fixing commit
    commit_msg: str                  # commit message of the fixing commit
    cvss_vector: str                 # full CVSS vector string
    cvss_base_score: float
    cvss_base_severity: str          # e.g. "HIGH", "MEDIUM"
    cvss_is_v3: bool                 # True if score is CVSSv3


class FuncDir(BaseModel):
    source: Path                        # <hash>_<func>_s.java
    target: Path                        # <hash>_<func>_t.java
    change_frequency_bug_fixing: Path   # change_frequency_bug_fixing.json
    change_frequency_bug_inducing: Path # change_frequency_bug_inducing.json
    metadata: Path                      # metadata.json


class HashDir(BaseModel):
    func_dirs: List[FuncDir]


class ComingPairDataDir(BaseModel):
    HashDirs: List[HashDir]


class ComingChangeFrequencyEntry(BaseModel):
    """Single entry from Coming 'frequency' list."""

    c: str = Field(..., description="The operation/entity affected by an action")
    f: int = Field(..., ge=0, description="The frequency of the action")


class ComingFrequencyParentEntry(BaseModel):
    c: str = Field(..., description="Parent-level pattern/entity string")
    f: int = Field(..., ge=0, description="The frequency of the action")


class ComingChangeFrequency(BaseModel):
    """Validated representation of Coming's change_frequency.json."""

    model_config = ConfigDict(extra="ignore", populate_by_name=True)

    frequency: List[ComingChangeFrequencyEntry] = Field(default_factory=list)
    # Coming commonly uses `frequencyParent`; accept both.
    frequency_parent: List[ComingFrequencyParentEntry] = Field(
        default_factory=list,
        alias="frequencyParent",
    )


class ComingRunResult(BaseModel):
    model_config = ConfigDict(extra="ignore")

    pair_dir: Path
    success: bool
    error: Optional[str] = None
    returncode: Optional[int] = None

    change_frequency_path: Optional[Path] = None
    change_frequency: Optional[ComingChangeFrequency] = None

    stdout: str = ""
    stderr: str = ""

    def total_changes(self) -> int:
        if self.change_frequency is None:
            return 0
        return sum(entry.f for entry in self.change_frequency.frequency)
