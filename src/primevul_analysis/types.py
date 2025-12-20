from __future__ import annotations

from pathlib import Path
from typing import List, Optional

from pydantic import BaseModel, ConfigDict, Field


class CodePair(BaseModel):
    id: str
    vulnerable_code: str
    patched_code: str
    vulnerable_hash: int
    patched_hash: int
    project: str
    cve: str
    cwe: List[str]
    commit_id: str


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