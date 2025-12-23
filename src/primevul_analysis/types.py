from pathlib import Path
from typing import List, Optional, Dict, Tuple

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
    
# REFACTORING NOTE: Rename to something like GumTreeToolOutput to better reflect its purpose.
class GumTreeDiffResult(BaseModel):
    """Result of a GumTree text diff operation."""

    model_config = ConfigDict(extra="ignore")

    file1: Path
    file2: Path
    diff_output: str
    diff_path: Optional[Path] = None

    error: Optional[str] = None
    returncode: Optional[int] = None
    stdout: str = ""
    stderr: str = ""

class NodeRef(BaseModel):
    """Reference to a node in a GumTree AST."""
    model_config = ConfigDict(frozen=True)

    type: str = Field(..., description="The type of the AST node (e.g., 'if_statement', 'identifier', etc).")
    label: Optional[str] = Field(None, description="The actual text/value (e.g. 'str_c', 'malloc'), if applicable.")
    start: int = Field(..., description="The start position of the node in the source code.")
    end: int = Field(..., description="The end position of the node in the source code.")

    @property
    def span(self) -> tuple[int, int]:
        return (self.start, self.end)
    
    @property
    def length(self) -> int:
        return self.end - self.start
    
class GumTreeMatch(BaseModel):
    """Represents a matched pair of nodes between source and destination ASTs."""

    src: NodeRef = Field(..., description="The node from the source AST.")
    dst: NodeRef = Field(..., description="The corresponding node from the destination AST.")

class GumTreeAction(BaseModel):
    """Represents a single action in a GumTree diff."""

    kind: str = Field(..., description="The kind of action (e.g., 'insert', 'delete', 'update', 'move').")
    tree: Optional[NodeRef] = Field(None, description="The AST node involved in the action.")
    parent: Optional[NodeRef] = Field(None, description="The parent AST node (for insert/move actions).")
    at: Optional[int] = Field(None, description="The position at which the action occurs, if applicable.")
    label: Optional[str] = Field(None, description="The label associated with the action, if any.")
    raw_attributes: Optional[Dict[str, str]] = Field(None, description="Raw attributes related to the action.")

class GumTreeDiff(BaseModel):
    """Structured representation of a GumTree diff."""

    actions: List[GumTreeAction] = Field(default_factory=list, description="All edit actions to transform source AST to destination AST.")
    matches: List[GumTreeMatch] = Field(default_factory=list, description="Pairs of matched nodes between source and destination ASTs.")

    # Convenience maps for quick lookup
    src_to_dst: Dict[Tuple[int, int], NodeRef] = Field(default_factory=dict, description="Lookup matched destination nodes by source node span.")
    dst_to_src: Dict[Tuple[int, int], NodeRef] = Field(default_factory=dict, description="Lookup matched source nodes by destination node span.")