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

## Coming Tool Guide

### What is Coming?

**Coming** is an extensible Java-based framework for mining and analyzing code changes at the Abstract Syntax Tree (AST) level. Developed by the SpoonLabs research group at INRIA, Coming provides automated pattern detection in code commits.

**Repository**: https://github.com/SpoonLabs/coming  
**Paper**: "Coming: A Tool for Mining Change Pattern Instances from Git Commits" (ASE 2019)  
**License**: GPL-1.0

**Key Capabilities**:
- AST-level diff analysis (beyond line-based git diff)
- Pattern mining across commit histories
- Change operation classification (Insert, Delete, Update, Move)
- Support for Java, C, and C++ (via Spoon and GumTree parsers)

**Why Use Coming for Vulnerability Analysis?**

Coming provides **semantic change detection** that captures security-relevant modifications:
- **Structural Changes**: Detects added `if` statements (NULL checks, bounds checks)
- **Operation Types**: Distinguishes insertions vs updates (new validation vs modified logic)
- **AST Node Types**: Identifies method invocations (safe function replacements), assignments (initialization), return statements (error handling)
- **Precision**: Ignores whitespace/comment changes, focuses on executable code

Example: Traditional diff shows "+5 lines", Coming shows "Insert IfStatement with BinaryOperator (== NULL)"

### Installation

**Prerequisites**:
- Java 11+ (JRE and JDK)
- Maven 3.6+ (if building from source)
- Git (for repository analysis)

**Option 1: Download Pre-built JAR**
```bash
# Latest release (check GitHub for current version)
wget https://github.com/SpoonLabs/coming/releases/download/v0.4.0/coming-0.4.0-jar-with-dependencies.jar

# Verify
java -jar coming-0.4.0-jar-with-dependencies.jar -help
```

**Option 2: Build from Source**
```bash
git clone https://github.com/SpoonLabs/coming.git
cd coming
mvn clean package -DskipTests

# JAR will be in target/ directory
ls target/coming-*-jar-with-dependencies.jar
```

**Verification**:
```bash
# Check Java version
java -version  # Should be 11+

# Test Coming
java -jar coming.jar -help
```

### Command-Line Usage

#### Basic Syntax

```bash
java -jar coming.jar [OPTIONS]
```

#### Core Arguments

**Required Arguments**:

- `-location <path>`: Path to git repository or directory containing code
  - Example: `-location /path/to/repo`
  - Example: `-location .` (current directory)

- `-mode <mode>`: Analysis mode
  - `diff`: Analyze code changes between commits (default)
  - `mineinstance`: Mine instances of change patterns
  - `features`: Extract code features from changes
  - `repairability`: Analyze repairability by automated repair tools

- `-input <type>`: Input type specification (optional, default: `git`)
  - `git`: Analyze git repository (default if not specified)
  - `filespair`: Analyze single pair of files (before/after)
  - `files`: Analyze files following specific directory hierarchy

**Common Optional Arguments**:

- `-parameters <key:value>`: Additional parameters (comma-separated)
  - `commit:<hash>`: Analyze specific commit (e.g., `commit:HEAD`, `commit:abc123`)
  - `branch:<name>`: Analyze specific branch (e.g., `branch:main`)
  - `file:<path>`: Analyze specific file
  - `maxrevisions:<n>`: Maximum number of commits to analyze
  - `outputformat:json`: Output format (json, xml, csv)
  
- `-output <path>`: Output directory for results
  - Example: `-output /tmp/coming_results`
  - Creates JSON/XML files with analysis results

- `-input <path>`: NOT for commit list input - see `-input <type>` above for input type specification

- `-filter <filter>`: Filter commits by criteria
  - Example: `-filter bugfix` (only commits with bug-related keywords)

### Input Types

Coming supports three input types specified via the `-input` argument:

#### 1. git (Default)

Analyze a git repository. If `-input` is not specified, `git` is used by default.

**Usage**:
```bash
# Implicit git mode
java -jar coming.jar -location /path/to/repo -mode diff

# Explicit git mode
java -jar coming.jar -input git -location /path/to/repo -mode diff
```

The `-location` path must be a valid git repository.

#### 2. filespair

Analyze a single pair of files (before/after versions). Useful for comparing two specific file versions without requiring a git repository.

**Usage**:
```bash
java -jar coming.jar \
  -input filespair \
  -location <source_file_path>:<target_file_path> \
  -mode diff
```

**Format**: `-location` takes the format `<source_file>:<target_file>` where:
- `source_file`: Path to the original/vulnerable version
- `target_file`: Path to the modified/patched version

**Example**:
```bash
java -jar coming.jar \
  -input filespair \
  -location /tmp/vulnerable.c:/tmp/patched.c \
  -mode diff \
  -output /output/analysis
```

**Advantages for PrimeVul**:
- No need to create temporary git repositories
- Direct file-to-file comparison
- Faster for single pair analysis
- Simpler workflow for batch processing

#### 3. files

Analyze multiple file pairs organized in a specific directory hierarchy.

**Usage**:
```bash
java -jar coming.jar \
  -input files \
  -location <location_arg> \
  -mode diff
```

**Directory Structure**:
The `-location` path should follow this hierarchy:
```
<location_arg>/
├── <diff_folder>/
│   ├── <nodiff_file>
│   │   ├── <diff_folder>_<nodiff_file>_s.java
│   │   └── <diff_folder>_<nodiff_file>_t.java
│   └── ...
└── ...
```

Where:
- `<diff_folder>`: Container directory for file pairs
- `<nodiff_file>`: Base name for the file pair
- `_s.java`: Source (before) version
- `_t.java`: Target (after) version

**Example Structure**:
```
/analysis/
├── pair_001/
│   └── code/
│       ├── pair_001_code_s.c
│       └── pair_001_code_t.c
├── pair_002/
│   └── function/
│       ├── pair_002_function_s.c
│       └── pair_002_function_t.c
```

**Batch Analysis Example**:
```bash
java -jar coming.jar \
  -input files \
  -location /analysis \
  -mode diff \
  -output /output/batch_results
```

Coming will analyze all pairs found in the hierarchy: `<diff_folder>/<nodiff_file>_s.java` vs `<diff_folder>/<nodiff_file>_t.java`

#### Usage Examples

**Analyze Latest Commit**:
```bash
java -jar coming.jar -location /path/to/repo -mode diff -parameters commit:HEAD
```

**Analyze Specific Commit**:
```bash
java -jar coming.jar \
  -location /path/to/repo \
  -mode diff \
  -parameters commit:a1b2c3d4 \
  -output /output/results
```

**Analyze Last 50 Commits**:
```bash
java -jar coming.jar \
  -location /path/to/repo \
  -mode diff \
  -parameters maxrevisions:50 \
  -output /output/batch_analysis
```

**Analyze with JSON Output**:
```bash
java -jar coming.jar \
  -location /path/to/repo \
  -mode diff \
  -parameters commit:HEAD,outputformat:json \
  -output /output/json_results
```

**Analyze Specific Branch**:
```bash
java -jar coming.jar \
  -location /path/to/repo \
  -mode diff \
  -parameters branch:develop,maxrevisions:100
```

### Analyzing Code Pairs (PrimeVul Use Case)

Coming supports multiple approaches for analyzing vulnerable/patched code pairs:

#### Method 1: Using filespair Input (Recommended for Single Pairs)

The simplest approach for analyzing a single code pair without creating a git repository:

**Step 1: Save Code to Files**
```bash
# Save vulnerable version
cat > /tmp/vulnerable.c << 'EOF'
void process(char *input) {
    char buffer[100];
    strcpy(buffer, input);  // Vulnerable
    printf("%s\n", buffer);
}
EOF

# Save patched version
cat > /tmp/patched.c << 'EOF'
void process(char *input) {
    char buffer[100];
    if (input == NULL) return;  // Added NULL check
    strncpy(buffer, input, 99);  // Safe function
    buffer[99] = '\0';
    printf("%s\n", buffer);
}
EOF
```

**Step 2: Run Coming with filespair**
```bash
java -jar /path/to/coming.jar \
  -input filespair \
  -location /tmp/vulnerable.c:/tmp/patched.c \
  -mode diff \
  -output /output/analysis
```

#### Method 2: Using files Input (Recommended for Batch Processing)

For analyzing multiple pairs efficiently:

**Step 1: Organize Files in Hierarchy**
```bash
mkdir -p /analysis/cve_2024_001/func
echo "void vuln() { strcpy(buf, src); }" > /analysis/cve_2024_001/func/cve_2024_001_func_s.c
echo "void vuln() { strncpy(buf, src, 99); }" > /analysis/cve_2024_001/func/cve_2024_001_func_t.c

mkdir -p /analysis/cve_2024_002/func
echo "void check() { *ptr = 1; }" > /analysis/cve_2024_002/func/cve_2024_002_func_s.c
echo "void check() { if (ptr) *ptr = 1; }" > /analysis/cve_2024_002/func/cve_2024_002_func_t.c
```

**Step 2: Run Coming on All Pairs**
```bash
java -jar /path/to/coming.jar \
  -input files \
  -location /analysis \
  -mode diff \
  -output /output/batch_analysis
```

Coming will automatically detect and analyze all pairs in the directory structure.

#### Method 3: Using git Input (Original Method)

For full git repository analysis or when git history is needed:

**Step 1: Create Temporary Git Repository**
```bash
mkdir temp_repo && cd temp_repo
git init
git config user.name "Analysis"
git config user.email "analysis@example.com"
```

**Step 2: Commit Vulnerable Version**
```bash
# Copy or create vulnerable code
cat > code.c << 'EOF'
void process(char *input) {
    char buffer[100];
    strcpy(buffer, input);  // Vulnerable
    printf("%s\n", buffer);
}
EOF

git add code.c
git commit -m "vulnerable version"
```

**Step 3: Commit Patched Version**
```bash
# Update with patched code
cat > code.c << 'EOF'
void process(char *input) {
    char buffer[100];
    if (input == NULL) return;  // Added NULL check
    strncpy(buffer, input, 99);  // Safe function
    buffer[99] = '\0';
    printf("%s\n", buffer);
}
EOF

git add code.c
git commit -m "patched version"
```

**Step 4: Run Coming**
```bash
java -jar /path/to/coming.jar \
  -location . \
  -mode diff \
  -parameters commit:HEAD,outputformat:json \
  -output /output/analysis
```

### Output Format

Coming generates different output files depending on the mode:

**For `-mode diff`**: Creates `change_frequency.json` (see "Change Frequency Output" section below)

**For `-mode mineinstance`**: Creates `all_instances_found.json` with the following structure:

```json
{
  "instances": [
    {
      "revision": "commit_hash",
      "instance_detail": [
        {
          "pattern_action": "INS",
          "pattern_entity": {
            "entity_type": "BinaryOperator",
            "entity_new value": "*",
            "entity_role": "*",
            "entity_parent": "null"
          },
          "concrete_change": {
            "operator": "insert-node",
            "src_type": "BinaryOperator",
            "dst_type": "null",
            "src": "i + 1",
            "dst": "null",
            "src_parent_type": "Invocation",
            "dst_parent_type": "null",
            "src_parent": "cs.charAt(i + 1)",
            "dst_parent": "null"
          },
          "file": "/path/to/file.java",
          "line": 171
        }
      ]
    }
  ]
}
```

**For `-mode repairability`**: Creates `all_instances_found.json` with repairability analysis showing which repair tools could have generated each commit

**For `-mode features`**: Creates `features_<commit_hash>.json` files (one per revision) with extracted code features. Structure:

```json
{
  "id": "commit_hash",
  "files": [
    {
      "file_name": "/path/to/changed/file.java",
      "features": [
        {
          "FEATURES_BINARYOPERATOR": {
            "0_cs == null": {
              "O1_IS_BIT": "false",
              "O1_IS_COMPARE": "true",
              "O1_IS_LOGICAL": "false",
              "O1_IS_MATH": "false",
              "O3_CONTAIN_NULL": "true"
            }
          },
          "FEATURES_METHODS": {
            "charAt": {
              "M8_RETURN_PRIMITIVE": "true",
              "M13_Argument_Has_Primitive": "true"
            }
          },
          "FEATURES_VARS": { ... },
          "S1_LOCAL_VAR_NOT_USED": "true",
          "S3_TYPE_OF_FAULTY_STATEMENT": "...",
          "ast_info": { ... }
        }
      ]
    }
  ]
}
```

Feature categories include:
- `FEATURES_BINARYOPERATOR`: Binary operator characteristics (bit, comparison, logical, math operations)
- `FEATURES_METHODS`: Method characteristics (overloading, return types, guards, parameters)
- `FEATURES_METHOD_INVOCATION`: Method call features
- `FEATURES_TYPEACCESS`: Type usage patterns
- `FEATURES_VARS`: Variable characteristics
- `S*_` patterns: Security and code quality patterns (null guards, exception handling, variable usage, etc.)

**Common Operation Types in Coming**:
- `insert-node` (or `INS`): New code added
- `delete-node` (or `DEL`): Code removed  
- `update-node` (or `UPD`): Code modified
- `move-tree` (or `MOV`): Code relocated

**Common AST Node Types**:
- `IfStatement`: Conditional statements
- `MethodInvocation`: Function/method calls
- `BinaryOperator`: Operators like `==`, `!=`, `>`, `<`, `+`, `-`
- `Assignment`: Variable assignments
- `ReturnStatement`: Return statements
- `Literal`: Constants (numbers, strings, NULL)
- `Block`: Code blocks `{}`
- `VariableDeclaration`: Variable declarations
- `ForStatement`, `WhileStatement`: Loops

### Change Frequency Output

When running Coming in `-mode diff`, it also generates a `change_frequency.json` file that shows the frequency and probability of each type of change.

**File**: `output/change_frequency.json`

**Structure**:
```json
{
  "frequency": [
    {
      "c": "BinaryOperator",
      "f": "6"
    },
    {
      "c": "TernaryOperator", 
      "f": "2"
    },
    {
      "c": "IF",
      "f": "2"
    }
  ],
  "frequencyParent": [
    {
      "c": "INS Invocation Block",
      "f": "2"
    },
    {
      "c": "UPD_BinaryOperator_If",
      "f": "2"
    },
    {
      "c": "INS If Block",
      "f": "2"
    }
  ]
}
```

**Field Descriptions**:

**`frequency` array**: Frequency of affected entities (AST node types)
- `c` (string): Entity type name (e.g., "BinaryOperator", "IfStatement", "MethodInvocation")
- `f` (string): Number of times this entity was affected by changes

**`frequencyParent` array**: Frequency of actions over affected entities and their parent types
- `c` (string): Pattern in format `<operation>_<NodeType>_<ParentType>`
  - Examples: "insert-node_If_Block", "update-node_BinaryOperator_If", "move-tree_VariableRead_Invocation"
- `f` (string): Number of times this pattern occurred

**Use Cases for Vulnerability Analysis**:

1. **Pattern Discovery**: Identify most common types of security fixes
   - High frequency of "INS IfStatement" → NULL/bounds checks commonly added
   - High frequency of "UPD MethodInvocation" → Unsafe functions commonly replaced

2. **CWE-Specific Patterns**: Compare frequency distributions across vulnerability types
   - Buffer overflows (CWE-119) may show more "UPD MethodInvocation" (strcpy→strncpy)
   - NULL pointer dereferences (CWE-476) may show more "INS IfStatement Block"

3. **Feature Selection**: Use high-frequency patterns as treatment variables
   - Focus causal analysis on patterns that occur frequently enough for statistical power

4. **Validation**: Cross-check extracted features against frequency distributions
   - If "INS IfStatement" is frequent but `added_null_check` feature has few positives, investigate extraction logic

**Example Analysis**:
```bash
# Generate frequency analysis for a batch of commits
java -jar coming.jar \
  -input files \
  -location /analysis/primevul_pairs \
  -mode diff \
  -output /output/frequency_analysis

# The change_frequency.json will show aggregate patterns across all analyzed pairs
cat /output/frequency_analysis/change_frequency.json
```

### JVM Options

**Memory Configuration**:
```bash
# Increase heap size for large repositories
java -Xmx4G -jar coming.jar -location /large/repo -mode diff

# Minimum and maximum heap
java -Xms512M -Xmx2G -jar coming.jar -location /repo -mode diff
```

**Performance Tuning**:
```bash
# Garbage collection tuning
java -XX:+UseG1GC -Xmx2G -jar coming.jar -location /repo -mode diff

# Enable parallel GC
java -XX:+UseParallelGC -Xmx2G -jar coming.jar -location /repo -mode diff
```

### Performance Characteristics

**Processing Time**:
- Simple changes (1-10 operations): 1-2 seconds
- Medium changes (10-100 operations): 2-5 seconds
- Complex changes (100+ operations): 5-30 seconds
- JVM startup overhead: ~1-2 seconds per invocation

**Memory Usage**:
- Base: ~100-200 MB
- Per commit: ~50-100 MB
- Large files (>1000 LOC): up to 500 MB

**Limitations**:
- Requires valid git repository
- C/C++ support via GumTree (may struggle with macros/preprocessor directives)
- Performance degrades with very large files (>5000 LOC)
- Each invocation starts new JVM (overhead for batch processing)

### Troubleshooting

**1. Java Not Found**
```bash
# Check Java installation
java -version

# Set JAVA_HOME if needed
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
```

**2. Out of Memory Error**
```bash
# Increase heap size
java -Xmx4G -jar coming.jar -location /repo -mode diff
```

**3. Parser Errors for C/C++**
- Coming may fail on complex preprocessor macros
- Try simplifying code or using preprocessed source
- Fallback: Use line-based diff for problematic files

**4. Empty Output**
- Ensure code actually differs between commits
- Check that git repository is valid: `git log`
- Verify file types are supported (C, C++, Java)

**5. Timeout Issues**
- Large files may take 30+ seconds
- Consider splitting large functions
- Use timeout wrapper in calling script

**6. No Operations Detected**
- Coming ignores whitespace-only changes
- Check that changes are semantic (not just formatting)
- Verify correct commit hash: `git show <hash>`

### Security-Relevant Patterns

**Common patterns Coming can detect for vulnerability analysis**:

| Pattern | Operation Type | Node Types | Example |
|---------|---------------|------------|---------|
| NULL check addition | Insert | IfStatement + BinaryOperator | `if (ptr == NULL) return;` |
| Bounds check addition | Insert | IfStatement + BinaryOperator | `if (size > MAX) return -1;` |
| Safe function replacement | Update | MethodInvocation | `strcpy()` → `strncpy()` |
| Return value check | Insert | IfStatement + MethodInvocation | `if (func() < 0) return;` |
| Variable initialization | Insert/Update | Assignment + Literal | `ptr = NULL;` |
| Error handling | Insert | ReturnStatement / IfStatement | `return -EINVAL;` |
| Memory cleanup | Insert | MethodInvocation | `free(ptr); ptr = NULL;` |
| Size calculation | Insert | MethodInvocation | `strlen()`, `sizeof()` |

### Additional Resources

- **GitHub Repository**: https://github.com/SpoonLabs/coming
- **Documentation**: See repository README and Wiki
- **Paper**: Matias Martinez et al., "Coming: A Tool for Mining Change Pattern Instances from Git Commits", ASE 2019
- **Spoon Framework**: https://spoon.gforge.inria.fr/ (underlying AST library)
- **GumTree**: https://github.com/GumTreeDiff/gumtree (C/C++ parser)

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
