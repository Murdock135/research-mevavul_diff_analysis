from typing import List, Dict, Any, Optional
from dataclasses import dataclass
from pydantic import BaseModel

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
    