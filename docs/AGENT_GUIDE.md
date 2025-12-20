# Agent Guide: PrimeVul Analysis Project

## Project Overview

**Purpose**: Analyze vulnerability fix patterns in the PrimeVul dataset using causal inference methods to identify effective security patch strategies.

**Dataset**: [PrimeVul v0.1](https://github.com/DLVulDet/PrimeVul) - 2,623 paired vulnerable/patched code snippets across 112 CWE types

**Tools**: 
- [Coming](https://github.com/SpoonLabs/coming) - AST-based code change analyzer
- Python data science stack (pandas, scikit-learn, DoWhy)

**Timeline**: 2-day sprint (see [`chats/2day_plan.md`](../chats/2day_plan.md))


## Coding Standards

- Use modular code and OOP principles with SOLID design

### Python Style

**Follow PEP 8** with these specifics:

- **Line length**: 88 characters (Black formatter default)
- **Imports**: Group stdlib, third-party, local (separated by blank lines)
- **Type hints**: Use for all function signatures
- **Docstrings**: Google style for all public functions/classes

```python
from typing import List, Dict, Any, Optional
import logging

import pandas as pd
import numpy as np

from primevul_analysis.patterns import extract_patterns


def extract_features(
    records: List[Dict[str, Any]], 
    use_coming: bool = True,
    timeout: int = 30
) -> pd.DataFrame:
    """Extract features from vulnerable/patched code pairs.
    
    Args:
        records: List of PrimeVul records with 'func' and 'target' fields
        use_coming: Whether to use Coming tool for AST analysis
        timeout: Maximum seconds per Coming invocation
        
    Returns:
        DataFrame with treatment variables and confounders
        
    Raises:
        ValueError: If records list is empty
    """
    pass
```

### Error Handling

**Principle**: Fail gracefully, log extensively, continue processing

```python
# ✅ GOOD: Log and continue
try:
    result = coming_extractor.analyze_pair(vuln, patched)
    if result.success:
        features.update(result.features)
except Exception as e:
    logger.error(f"Coming failed for {record['cve']}: {e}")
    features['coming_success'] = False
    # Fall back to regex patterns
    features.update(regex_extractor.extract(vuln, patched))

# ❌ BAD: Silent failure
try:
    result = coming_extractor.analyze_pair(vuln, patched)
except:
    pass

# ❌ BAD: Stop entire pipeline on single failure
result = coming_extractor.analyze_pair(vuln, patched)  # May raise
```

### Logging

**Use structured logging** with appropriate levels:

```python
import logging

logger = logging.getLogger(__name__)

# Configuration
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)

# Usage
logger.debug(f"Processing record {idx}: {record['cve']}")  # Verbose details
logger.info(f"Processed 100/2623 pairs (3.8%)")  # Progress updates
logger.warning(f"Coming timeout for {cve}, using regex fallback")  # Recoverable issues
logger.error(f"Failed to parse record {idx}: {error}")  # Errors with context
```

### Data Handling

**Use appropriate data structures**:

- **Raw data**: Keep JSONL for disk efficiency
- **Processing**: Convert to pandas DataFrame for analysis
- **Large datasets**: Use Parquet for columnar storage (faster, compressed)
- **Results**: CSV for human readability, Parquet for programmatic access

```python
# ✅ GOOD: Load efficiently
def load_primevul_data(jsonl_path: str) -> List[Dict[str, Any]]:
    """Load PrimeVul JSONL data."""
    records = []
    with open(jsonl_path) as f:
        for line in f:
            if line.strip():
                records.append(json.loads(line))
    return records

# Save with compression
df.to_parquet('output/features.parquet', compression='snappy', index=False)

# ❌ BAD: Load entire file into memory at once
with open(jsonl_path) as f:
    data = json.load(f)  # Wrong: JSONL is not valid JSON
```

## Dataset Structure

### PrimeVul v0.1 Overview

**Source**: [PrimeVul Dataset](https://github.com/DLVulDet/PrimeVul)

**Size**:
- Total: 233,941 records
- Training: 175,797 (7,578 paired)
- Validation: 23,948 (960 paired)  
- Test: 24,788 (870 paired)
- **Paired Total**: 9,408 vulnerable/patched code pairs

**Coverage**: 112 open-source projects, 112 CWE types, primarily C/C++  
**Time Period**: Historical CVEs from 2000s-2021

### File Organization

```
PrimeVul_v0.1/
├── primevul_train.jsonl          # Training set (all vulnerable instances)
├── primevul_train_paired.jsonl   # Training pairs (vulnerable + patched)
├── primevul_valid.jsonl          # Validation set
├── primevul_valid_paired.jsonl   # Validation pairs
├── primevul_test.jsonl           # Test set
├── primevul_test_paired.jsonl    # Test pairs
├── file_info.json                # Source file metadata
└── file_contents/                 # Full source files by project
    ├── openssl/
    ├── linux-kernel/
    └── ...
```

**File Formats**:
- `.jsonl`: One JSON object per line (streaming-friendly)
- `file_info.json`: Standard JSON with file metadata
- `file_contents/`: Plain text source files

## Data Schemas

### PrimeVul JSONL Record Schema

Each record in paired JSONL files contains:

```json
{
  "idx": 0,
  "project": "openssl",
  "commit_id": "ca989269a2876bae79393bd54c3e72d49975fc75",
  "project_url": "https://github.com/openssl/openssl",
  "commit_url": "https://git.openssl.org/...",
  "commit_message": "Use version in SSL_METHOD not SSL structure...",
  "target": 1,
  "func": "long ssl_get_algorithm2(SSL *s) {...}",
  "func_hash": 255087747659226932756944884868284698117,
  "file_name": "None",
  "file_hash": null,
  "cwe": ["CWE-310"],
  "cve": "CVE-2013-6449",
  "cve_desc": "The ssl_get_algorithm2 function in ssl/s3_lib.c...",
  "nvd_url": "https://nvd.nist.gov/vuln/detail/CVE-2013-6449"
}
```

**Field Descriptions**:
- `idx` (int): Unique identifier within file
- `project` (str): Source project name
- `commit_id` (str): Git commit hash
- `project_url` (str): Repository URL
- `commit_url` (str): Full commit URL
- `commit_message` (str): Commit description
- `target` (int): **Critical** - 0 = vulnerable, 1 = patched
- `func` (str): **Primary data** - Complete function source code
- `func_hash` (int): Function code hash for deduplication
- `file_name` (str): Source file path (may be "None")
- `file_hash` (int|null): File hash
- `cwe` (list[str]): CWE categories (e.g., ["CWE-119"])
- `cve` (str): CVE identifier
- `cve_desc` (str): Vulnerability description
- `nvd_url` (str): NVD link

### Data Pairing

**Pairing Logic**:
- Each vulnerability has 2 records: `target=0` (vulnerable) + `target=1` (patched)
- Match using composite key: `(cve, project, commit_id)`
- `func` field contains before/after versions of same function

### CWE Distribution

| CWE ID | Category | Count | % |
|--------|----------|-------|---|
| CWE-119 | Buffer Errors | ~1500 | 20% |
| CWE-125 | Out-of-bounds Read | ~800 | 11% |
| CWE-20 | Input Validation | ~600 | 8% |
| CWE-416 | Use After Free | ~400 | 5% |
| CWE-476 | NULL Deref | ~350 | 5% |
| CWE-190 | Integer Overflow | ~300 | 4% |
| CWE-200 | Info Exposure | ~280 | 4% |
| CWE-120 | Buffer Copy | ~250 | 3% |
| CWE-399 | Resource Mgmt | ~200 | 3% |
| CWE-189 | Numeric Errors | ~180 | 2% |

**Long Tail**: 102 additional CWE types with <150 instances each

**Implications**: Focus on top 3-5 CWEs for statistical power

### Data Quality

**Code Characteristics**:
- ✅ Complete, compilable functions from actual commits
- ⚠️ May include project-specific headers/macros
- ⚠️ Some functions >1000 LOC

**Known Issues**:
1. Missing `file_name` (some "None" or null)
2. Multiple CWEs per CVE
3. Code duplicates across projects (check `func_hash`)
4. Whitespace/formatting variations between versions

**Function Size**:
- Median: 50 lines
- Mean: 120 lines
- 90th percentile: 300 lines
- Max: 5000+ lines (rare)

**Project Distribution**:
- linux-kernel (~15%)
- FFmpeg (~8%)
- openssl (~6%)
- Others distributed across 109 projects

### Common Fix Patterns

**Pattern Categories**:
- **NULL Check Addition**: Pointer validation before dereference
- **Bounds Check Addition**: Array/buffer size validation
- **Safe Function Replacement**: strcpy→strncpy, sprintf→snprintf
- **Error Handling**: Return value checks, error propagation
- **Input Validation**: Parameter validation
- **Memory Cleanup**: Proper resource release

### Extracted Features Schema

After processing, each record has ~65-70 features:

**Identifiers** (5 fields):
- `idx`, `cve`, `cwe`, `project`, `commit_id`, `target`

**Treatment Variables** (9 boolean fields - what was changed):
- `added_null_check`: if (ptr == NULL)
- `added_bounds_check`: if (size > MAX)
- `replaced_unsafe_func`: strcpy→strncpy
- `added_error_handling`: return -1, goto error
- `added_return_check`: Checking return values
- `added_input_validation`: Parameter checks
- `added_size_calculation`: sizeof(), strlen()
- `added_initialization`: NULL/0 initialization
- `added_memory_cleanup`: free() calls

**Confounders** (10+ fields - pre-patch state):
- `vuln_loc`: Lines of code
- `vuln_cyclomatic`: Complexity
- `vuln_num_if`, `vuln_num_for`, `vuln_num_while`: Control flow
- `vuln_num_functions`: Function calls
- `vuln_has_malloc`, `vuln_has_pointer`, `vuln_has_array`: Code patterns
- `vuln_has_string_op`: String operations
- `vuln_num_params`: Parameter count

**Patch Characteristics** (4 fields):
- `patch_lines_added`, `patch_lines_deleted`, `patch_net_change`, `patch_num_hunks`

**Coming Features** (30+ fields - AST operations):
- `coming_success`: bool
- `coming_total_operations`: int
- `coming_ins_*`: Insertions (IfStatement, ReturnStatement, Assignment, etc.)
- `coming_del_*`: Deletions
- `coming_upd_*`: Updates (MethodInvocation, Literal, BinaryOperator, etc.)

**Metadata** (3 fields):
- `processing_time_ms`, `extraction_method`, `error`, `warning`

**Note**: Coming features only available when `coming_success=True`

## Data Processing Strategies

### Loading
- **Format**: JSONL (one JSON per line)
- **Memory**: Paired files (~9K records) fit in RAM; full dataset (~234K) needs streaming
- **Preprocessing**: Extract primary CWE, calculate `func_loc`, handle missing fields, deduplicate by `func_hash`

### Pairing
- **Method 1**: DataFrame merge on `(cve, project, commit_id)` with suffixes `_vuln`/`_patched`
- **Method 2**: Dictionary grouping, extract `(vulnerable_record, patched_record)` tuples

### Filtering
- **By CWE**: Top 3-5 categories for statistical power
- **By Project**: Domain-specific or cross-project analysis
- **By Size**: <100 LOC (simple), <1000 LOC (exclude outliers)
- **By Quality**: Remove null/empty `func`, validate metadata

### Diff Analysis
- Line-by-line comparison using `difflib.unified_diff`
- Count added/deleted/modified lines
- Extract security-relevant changes (NULL checks, bounds checks, function replacements)

### Validation
- Equal counts of `target=0` and `target=1`
- Check for NULL/empty `func`
- Validate CWE format
- Identify duplicates
- Report unpaired records

### Persistence
- **Parquet**: Processed features (fast, compressed, typed)
- **CSV**: Results export
- **JSONL**: Intermediate steps
- **Batch Processing**: 500-1000 record chunks for large datasets

## Resources

- **PrimeVul Paper**: https://arxiv.org/abs/2108.09453
- **Coming GitHub**: https://github.com/SpoonLabs/coming
- **DoWhy Documentation**: https://microsoft.github.io/dowhy/
- **Project Timeline**: [`chats/2day_plan.md`](../chats/2day_plan.md)

---

**Last Updated**: December 19, 2025  
**Version**: 2.0
