# Feature Matrix Exploration

**File**: `data/processed/feature_matrix.parquet`
**Shape**: 4,866 rows × 513 columns (496 features + 17 metadata)

---

## Dataset Composition

| Metric | Value |
|---|---|
| Total rows | 4,866 |
| Vulnerable functions | 2,433 |
| Patched functions | 2,433 |
| Unique CVEs | 775 |
| Unique commits | 902 |
| Unique repos | 362 |

Labels are perfectly balanced (1:1 vul/patch). Each pair (vulnerable function, patched function) shares the same commit metadata.

---

## Metadata Columns

| Column | Type | Notes |
|---|---|---|
| `is_vul` | bool | True = vulnerable version |
| `id` | str | Function identifier |
| `func_name` | str | Java function name |
| `cve_id` | str | CVE identifier |
| `cwe_ids` | str (list) | CWE tags (parsed as list) |
| `commit_hash` | str | Fixing commit |
| `parent_commit_hash` | str | Vulnerable commit |
| `repo_name` | str | GitHub org/repo |
| `git_url` | str | Repository URL |
| `file_path` / `file_name` | str | Source location |
| `commit_date` | int | Unix timestamp |
| `commit_msg` | str | Commit message |
| `cvss_vector` | str | Full CVSS vector string |
| `cvss_base_score` | float | 1.2–10.0 |
| `cvss_base_severity` | str | LOW / MEDIUM / HIGH / CRITICAL |
| `cvss_is_v3` | bool | True if CVSSv3 |

No nulls in any metadata column.

---

## Feature Columns

All 496 feature columns encode Coming AST diff operation counts with the naming scheme:

```
<action>_<NodeType>_<ParentContext>
```

### Action type breakdown

| Action | # features | Total count across dataset |
|---|---|---|
| `delete-node` | 137 | 3,793 |
| `insert-node` | 136 | 3,772 |
| `update-node` | 93 | 3,187 |
| `move-tree` | 130 | 3,143 |

### Sparsity

The feature matrix is extremely sparse:

- 485 / 496 features (97.8%) have >95% zero values
- 427 / 496 features (86.1%) have >99% zero values
- No feature is all-zero (every operation type occurred at least once)

### Per-row change counts

Total AST operations per function (sum across all feature columns):

| Stat | Value |
|---|---|
| min | 0 |
| 25th pct | 1 |
| median | 2 |
| mean | 2.86 |
| 75th pct | 3 |
| max | 36 |

303 rows (6.2%) have zero changes — these are function pairs where Coming produced no AST operations, likely due to parsing failures or trivial whitespace-only diffs.

---

## Top 20 Features by Total Count

| Feature | Total count |
|---|---|
| `delete-node_If_Block` | 599 |
| `insert-node_If_Block` | 593 |
| `delete-node_Invocation_Block` | 552 |
| `insert-node_Invocation_Block` | 545 |
| `delete-node_LocalVariable_Block` | 498 |
| `insert-node_LocalVariable_Block` | 498 |
| `move-tree_VariableRead_Invocation` | 390 |
| `update-node_TypeAccess_Invocation` | 327 |
| `update-node_VariableRead_Invocation` | 294 |
| `move-tree_Invocation_Invocation` | 277 |
| `move-tree_Invocation_Block` | 250 |
| `update-node_Literal_Invocation` | 227 |
| `update-node_Invocation_LocalVariable` | 214 |
| `move-tree_Invocation_LocalVariable` | 203 |
| `update-node_Invocation_Block` | 191 |
| `update-node_TypeReference_LocalVariable` | 189 |
| `insert-node_Invocation_Invocation` | 186 |
| `update-node_Invocation_Invocation` | 183 |
| `delete-node_Invocation_Invocation` | 181 |
| `insert-node_VariableRead_Invocation` | 151 |

---

## Features Most Discriminative Between Vul and Patch

Ranked by `|mean(vul) - mean(patch)|` — the mean absolute difference in feature value between the vulnerable and patched versions of the same functions:

| Feature | \|mean_vul − mean_patch\| |
|---|---|
| `delete-node_If_Block` | 0.1566 |
| `insert-node_If_Block` | 0.1558 |
| `delete-node_Invocation_Block` | 0.1184 |
| `insert-node_Invocation_Block` | 0.1180 |
| `insert-node_LocalVariable_Block` | 0.0838 |
| `delete-node_LocalVariable_Block` | 0.0814 |
| `insert-node_VariableRead_Invocation` | 0.0316 |
| `delete-node_VariableRead_Invocation` | 0.0312 |
| `insert-node_Invocation_Invocation` | 0.0304 |
| `delete-node_Invocation_Invocation` | 0.0292 |
| `delete-node_Invocation_LocalVariable` | 0.0218 |
| `insert-node_Invocation_LocalVariable` | 0.0218 |
| `insert-node_Parameter_Method` | 0.0206 |
| `delete-node_Parameter_Method` | 0.0197 |
| `delete-node_BinaryOperator_If` | 0.0181 |

The top discriminators are `If_Block` and `Invocation_Block` operations — consistent with the intuition that security patches add or restructure `if`-guarded checks and method calls.

> Note: because each pair contributes both a vulnerable and a patched row, the mean difference captures the within-patch asymmetry (e.g., the patch inserts an `If` node, so the patched row has `insert-node_If_Block = 1` while the vulnerable row has `delete-node_If_Block = 1` in the bug-fixing diff direction). Differences are therefore expected to be small in absolute terms but consistent in direction.

---

## CWE Distribution (Top 20)

| CWE | Row count |
|---|---|
| CWE-79 (XSS) | 564 |
| CWE-611 (XXE) | 420 |
| CWE-22 (Path Traversal) | 358 |
| CWE-89 (SQL Injection) | 344 |
| CWE-Other | 300 |
| CWE-264 (Permissions) | 206 |
| CWE-502 (Deserialization) | 182 |
| CWE-400 (Resource Exhaustion) | 162 |
| CWE-20 (Input Validation) | 162 |
| CWE-200 (Info Exposure) | 136 |
| CWE-668 (Exposure of Resource) | 120 |
| CWE-74 (Injection) | 116 |
| CWE-770 (Allocation without Limits) | 102 |
| CWE-863 (Incorrect Authorization) | 102 |
| CWE-787 (Out-of-bounds Write) | 96 |
| CWE-862 (Missing Authorization) | 92 |
| CWE-94 (Code Injection) | 88 |
| CWE-352 (CSRF) | 86 |
| CWE-918 (SSRF) | 80 |
| CWE-287 (Improper Authentication) | 74 |

Row counts include both the vulnerable and patched row for each function, so divide by ~2 for unique functions. CWE counts reflect the MegaVul dataset's Java-heavy composition (XSS, XXE, SQLi are web-prevalent).

---

## CVSS Distribution

| Severity | Count |
|---|---|
| MEDIUM | 2,616 |
| HIGH | 1,588 |
| LOW | 342 |
| CRITICAL | 320 |

CVSS base score: min = 1.2, mean = 6.27, median = 6.5, max = 10.0.
62% of entries use CVSSv2 scoring; 38% use CVSSv3.

---

## Observations and Caveats

1. **Zero-change rows**: 303 rows have no recorded AST operations. These should be investigated or filtered before modeling — they may indicate Coming parse failures (e.g., Spoon failing to parse the wrapped Java snippet).

2. **Extreme sparsity**: With ~97% of features being >95% zero, standard dense classifiers will struggle. Tree-based models (XGBoost, Random Forest) or dimensionality reduction are advisable.

3. **Feature symmetry**: Since diffs are computed in the bug-fixing direction (patch → vulnerable reversed), `insert-node_X` on the patched side mirrors `delete-node_X` on the vulnerable side. The matrix captures both sides of the pair, so many feature pairs will be near-mirror images.

4. **CWE-Other**: The "CWE-Other" category (300 rows) aggregates non-standard or composite CWE tags and may need manual inspection before CWE-stratified analysis.

5. **CVE coverage**: Mean rows per CVE ≈ 6.3 (both vul+patch), so ≈ 3 function pairs per CVE on average — some CVEs have up to 60 pairs (max rows = 120).
