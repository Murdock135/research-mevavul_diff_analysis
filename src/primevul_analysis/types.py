from typing import List, Dict, Any, Optional
from dataclasses import dataclass
from pydantic import BaseModel

class CodePair(BaseModel):
    id: str
    vulnerable_code: str
    patched_code: str
    vulnerable_hash: Optional[str]
    patched_hash: Optional[str]
    project: str
    cve: str
    cwe: str
    commit_id: str
    