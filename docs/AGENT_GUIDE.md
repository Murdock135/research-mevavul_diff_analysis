# Agent Guide: PrimeVul Analysis Project

## Project Overview

**Purpose**: Analyze vulnerability fix patterns in the PrimeVul dataset using causal inference methods to identify effective security patch strategies.

**Dataset**: [PrimeVul v0.1](https://github.com/DLVulDet/PrimeVul) - 2,623 paired vulnerable/patched code snippets across 112 CWE types

**Tools**: 
- [Coming](https://github.com/SpoonLabs/coming) - AST-based code change analyzer
- Python data science stack (pandas, scikit-learn, DoWhy)

**Timeline**: 2-day sprint (see [`chats/2day_plan.md`](../chats/2day_plan.md))

## Project Structure

```
/app/
├── src/primevul_analysis/     # Core analysis modules
│   ├── extract.py             # Feature extraction pipeline
│   ├── patterns.py            # Regex-based pattern detection
│   └── __init__.py
├── scripts/                   # Executable scripts
│   ├── quick_coming_test.py   # Coming validation (Day 1)
│   └── extract_all_features.py # Batch processing
├── notebooks/                 # Analysis notebooks
│   ├── explore.ipynb          # Initial exploration
│   ├── descriptive_analysis.ipynb  # Descriptive stats (Day 1)
│   └── causal_analysis.ipynb  # Causal inference (Day 2)
├── output/                    # Generated data
│   ├── features.parquet       # Extracted features
│   ├── processed_data.parquet # Treatment/confounder matrix
│   └── results.csv            # Effect estimates
├── figures/                   # Visualizations
│   ├── descriptive/
│   └── causal/
├── PrimeVul_v0.1/            # Dataset (JSONL + code)
└── pyproject.toml            # Dependencies
```

## Coding Standards

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

## Data Schemas

### PrimeVul JSONL Record

```json
{
  "cve": "CVE-2016-1234",
  "cwe": "CWE-119",
  "project": "linux-kernel",
  "commit_id": "abc123def456",
  "func": "int vulnerable_function() {...}",
  "target": "int patched_function() {...}",
  "file_name": "src/buffer.c",
  "idx": 42
}
```

### Extracted Features Schema

```python
{
    # Identifiers
    'cve': str,
    'cwe': str,
    'project': str,
    'commit_id': str,
    
    # Treatment variables (binary)
    'added_null_check': bool,
    'added_bounds_check': bool,
    'replaced_unsafe_func': bool,
    'added_error_handling': bool,
    'added_return_check': bool,
    
    # Confounders
    'vuln_loc': int,                    # Lines of code
    'vuln_num_if': int,                 # Conditionals
    'vuln_num_for': int,                # For loops
    'vuln_num_while': int,              # While loops
    'vuln_has_malloc': bool,            # Memory allocation
    'vuln_has_pointer': bool,           # Pointer usage
    
    # Coming features (if available)
    'coming_success': bool,
    'coming_ins_IfStatement': int,      # AST insertions
    'coming_del_Assignment': int,       # AST deletions
    'coming_upd_MethodInvocation': int, # AST updates
    
    # Processing metadata
    'error': Optional[str]
}
```

## Common Workflows

### 1. Testing Coming on Sample Data

```bash
# Create test script (Day 1, Hour 1)
cd /app
python scripts/quick_coming_test.py \
  --input PrimeVul_v0.1/primevul_train_paired.jsonl \
  --samples 10 \
  --output output/coming_test_results.json

# Evaluate success rate
# Decision: If >50%, proceed with Coming; else use regex only
```

### 2. Feature Extraction

```bash
# Small test batch
uv run python -m src.primevul_analysis.extract \
  --input PrimeVul_v0.1/primevul_train_paired.jsonl \
  --output output/features_test.parquet \
  --workers 4 \
  --limit 100

# Full extraction (if Coming works)
uv run python -m src.primevul_analysis.extract \
  --input PrimeVul_v0.1/primevul_train_paired.jsonl \
  --output output/features.parquet \
  --use-coming \
  --workers 8

# Regex-only fallback (if Coming fails)
uv run python -m src.primevul_analysis.extract \
  --input PrimeVul_v0.1/primevul_train_paired.jsonl \
  --output output/features.parquet \
  --workers 8
```

### 3. Descriptive Analysis

```python
# In notebooks/descriptive_analysis.ipynb
import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt

# Load features
df = pd.read_parquet('output/features.parquet')

# Filter to top CWEs
top_cwes = df['cwe'].value_counts().head(5).index
df_top = df[df['cwe'].isin(top_cwes)]

# Pattern frequency by CWE
pattern_cols = [c for c in df.columns if c.startswith('added_')]
pattern_freq = df_top.groupby('cwe')[pattern_cols].mean()

# Visualize
fig, ax = plt.subplots(figsize=(10, 6))
pattern_freq.plot(kind='bar', ax=ax)
plt.title('Fix Pattern Frequency by Top 5 CWEs')
plt.ylabel('Proportion of Patches')
plt.tight_layout()
plt.savefig('figures/descriptive/pattern_by_cwe.png', dpi=300)
```

### 4. Causal Analysis

```python
# In notebooks/causal_analysis.ipynb
from dowhy import CausalModel
import pandas as pd

df = pd.read_parquet('output/processed_data.parquet')

# Focus on top CWE
df_cwe119 = df[df['cwe'] == 'CWE-119']

# Define causal model
model = CausalModel(
    data=df_cwe119,
    treatment='added_null_check',
    outcome='patch_success',  # If available
    common_causes=['vuln_loc', 'vuln_num_if', 'vuln_has_pointer']
)

# Identify causal effect
identified_estimand = model.identify_effect()

# Estimate using propensity score matching
estimate = model.estimate_effect(
    identified_estimand,
    method_name="backdoor.propensity_score_matching"
)

print(estimate)
```

## Tool-Specific Guidelines

### Using Coming

**Installation**: Coming should be available at `/opt/coming.jar`

**Usage Pattern**:
```bash
java -jar /opt/coming.jar \
  -mode filespair \
  -location1 vuln.c \
  -location2 patched.c \
  -output output_dir/
```

**Expected Output**: JSON file with AST change operations

**Common Issues**:
- **Timeout**: Set 30s limit per pair, log and continue
- **Parse errors**: Coming may fail on complex macros or templates
- **No output**: Check stderr, log failure, use regex fallback

**Success Criteria**: >50% of test samples produce valid output

### Regex Pattern Detection (Fallback)

**Purpose**: Extract basic fix patterns without AST parsing

**Patterns** (from `src/primevul_analysis/patterns.py`):
- `null_check`: `if (ptr == NULL)` or `if (!ptr)`
- `bounds_check`: `if (size > MAX)` or `if (idx < len)`
- `safe_functions`: `strncpy`, `snprintf`, `strlcpy`
- `error_handling`: `return -1`, `goto error`, `exit(1)`

**Limitations**:
- Cannot detect semantic changes (logic fixes)
- May have false positives (commented code)
- Cannot track AST-level refactorings

**When to use**: Always run as baseline; use exclusively if Coming fails

## Testing Strategy

### Unit Tests
```python
# tests/test_extract.py
import pytest
from src.primevul_analysis.extract import RegexPatternExtractor

def test_null_check_detection():
    extractor = RegexPatternExtractor()
    
    # Positive cases
    code = "if (ptr == NULL) return -1;"
    features = extractor.extract_features(code)
    assert features['pattern_null_check'] > 0
    
    # Negative cases
    code = "int x = 5;"
    features = extractor.extract_features(code)
    assert features['pattern_null_check'] == 0

def test_treatment_extraction():
    extractor = RegexPatternExtractor()
    
    vuln = "strcpy(buf, src);"
    patched = "strncpy(buf, src, sizeof(buf));"
    
    treatments = extractor.extract_treatment_variables(vuln, patched)
    assert treatments['replaced_unsafe_func'] == True
```

### Integration Tests
```bash
# Test on known CVE with expected patterns
python scripts/test_known_cve.py --cve CVE-2016-5195
```

## Performance Optimization

### Parallel Processing
```python
from concurrent.futures import ProcessPoolExecutor

# ✅ GOOD: Process-based parallelism (avoids GIL)
with ProcessPoolExecutor(max_workers=8) as executor:
    futures = [executor.submit(extract_features, rec) for rec in records]
    results = [f.result() for f in futures]

# ❌ BAD: Thread-based (GIL bottleneck for CPU-bound tasks)
with ThreadPoolExecutor(max_workers=8) as executor:
    ...
```

### Memory Management
```python
# ✅ GOOD: Process in chunks
chunk_size = 500
for i in range(0, len(records), chunk_size):
    chunk = records[i:i+chunk_size]
    df_chunk = process_batch(chunk)
    df_chunk.to_parquet(f'output/features_part_{i}.parquet')

# Combine later
dfs = [pd.read_parquet(f) for f in glob('output/features_part_*.parquet')]
df_final = pd.concat(dfs, ignore_index=True)

# ❌ BAD: Load all 2623 pairs in memory at once
df = pd.DataFrame([process_pair(r) for r in records])  # OOM risk
```

## Reproducibility Checklist

- [ ] Pin all dependencies in `pyproject.toml`
- [ ] Set random seeds (`np.random.seed(42)`)
- [ ] Log software versions (Coming, Python, pandas)
- [ ] Save intermediate outputs (features, processed data)
- [ ] Document data filtering decisions
- [ ] Version control notebooks (clear outputs before commit)
- [ ] Include timestamp and git commit in reports

## Common Pitfalls

### 1. Coming Output Parsing
**Problem**: Coming JSON structure varies by version
**Solution**: Implement defensive parsing with try/except, log warnings

### 2. CWE Distribution
**Problem**: Heavy tail (few CWEs have many samples, most have <10)
**Solution**: Focus on top 3-5 CWEs (CWE-119, CWE-125, CWE-20)

### 3. Treatment Variable Correlation
**Problem**: Many fix patterns co-occur (multicollinearity)
**Solution**: Report correlations, consider composite treatments, use regularization

### 4. Confounding
**Problem**: LOC correlates with both treatment and outcome
**Solution**: Use propensity scores, include fixed effects, report conditional estimates

### 5. Outcome Variable
**Problem**: All patches are successful (no outcome variation)
**Solution**: Use pattern presence as outcome, or analyze pattern combinations

## Research Ethics

- **Attribution**: Cite PrimeVul dataset authors (cite.bib)
- **Reproducibility**: Make code publicly available (GitHub)
- **Transparency**: Report null results and limitations
- **Data**: Do not distribute PrimeVul data; point to original source
- **Claims**: Avoid causal language unless justified by methodology

## Quick Reference

### File Paths
```python
PRIMEVUL_DIR = Path('/app/PrimeVul_v0.1')
TRAIN_PAIRED = PRIMEVUL_DIR / 'primevul_train_paired.jsonl'
TEST_PAIRED = PRIMEVUL_DIR / 'primevul_test_paired.jsonl'
OUTPUT_DIR = Path('/app/output')
FIGURES_DIR = Path('/app/figures')
```

### Load/Save Patterns
```python
# Load JSONL
records = load_primevul_data(str(TRAIN_PAIRED))

# Save features
df.to_parquet(OUTPUT_DIR / 'features.parquet', index=False)

# Save results
results_df.to_csv(OUTPUT_DIR / 'results.csv', index=False)

# Save figure
plt.savefig(FIGURES_DIR / 'descriptive' / 'fig1.png', dpi=300, bbox_inches='tight')
```

### Useful Filters
```python
# Top N CWEs by frequency
top_cwes = df['cwe'].value_counts().head(3).index
df_top = df[df['cwe'].isin(top_cwes)]

# Successful Coming analyses only
df_coming = df[df['coming_success'] == True]

# Records with specific treatment
df_treated = df[df['added_null_check'] == True]

# Clean data (no errors)
df_clean = df[df['error'].isna()]
```

## Resources

- **PrimeVul Paper**: https://arxiv.org/abs/2108.09453
- **Coming GitHub**: https://github.com/SpoonLabs/coming
- **DoWhy Documentation**: https://microsoft.github.io/dowhy/
- **Project Timeline**: [`chats/2day_plan.md`](../chats/2day_plan.md)

## Support

For issues or questions:
1. Check terminal output logs
2. Verify Coming installation: `coming -showactions`
3. Test on single example before batch processing
4. Review error logs in `output/` directory
5. Consult `2day_plan.md` for decision points (e.g., Coming pivot)

---

**Last Updated**: December 19, 2025  
**Version**: 1.0  
**Maintainer**: Project Team
