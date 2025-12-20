from typing import List, Dict, Any, Optional
from dataclasses import dataclass
from pydantic import BaseModel, Field, 

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
    c: str = Field(..., description="The operation being affected by an action")
    f: int = Field(..., description="The frequency of the action")

class ComingFrequencyParentEntry(BaseModel):
    c: str = Field(..., description="The entity being effected in the parent by an action")
    f: int = Field(..., description="The frequency of the action")

class ComingChangeFrequency(BaseModel):
    frequency: List[ComingChangeFrequencyEntry] = Field(default_factory=list, description="List of frequency entries")
    frequency_parent: List[ComingFrequencyParentEntry] = Field(default_factory=list, description="List of frequency parent entries")

class ComingRunResult(BaseModel):
    pair_dir: str
    success: bool
    error: Optional[str]
    returncode: Optional[int] = None

    change_frequency_path : Optional[str] = None
    change_frequency: Optional[ComingChangeFrequency] = None

    stdout: str = ""
    stderr: str = ""

    def total_changes(self) -> int:
        if self.change_frequency is None:
            return 0
        return sum(entry.f for entry in self.change_frequency.frequency)