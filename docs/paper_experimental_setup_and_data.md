# Experimental Setup and Data

> **Status**: draft — placeholders marked `[TODO]` where results are pending.

---

## 3. Experimental Setup and Data

### 3.1 Dataset

We use the **MegaVul** dataset [CITE], which provides CVE-linked pairs of vulnerable and patched Java functions extracted from open-source GitHub repositories.
Each entry in MegaVul links a CVE to one or more security-fixing commits; for each commit, functions that were modified are recorded as `(func_before, func_after)` pairs, together with the full CVE metadata (CWE identifiers, CVSS score and severity, commit hash, repository URL, and commit message).

After extracting all Java function pairs we obtain **4,866 samples** spanning **775 unique CVEs**, **902 commits**, and **362 repositories**.
The dataset is perfectly balanced by construction: for every vulnerable function there is a corresponding patched function, yielding 2,433 vulnerable and 2,433 patched instances.
Table 1 summarises the dataset.

**Table 1 — Dataset summary.**

| Metric | Value |
|---|---|
| Total samples | 4,866 |
| Vulnerable functions | 2,433 |
| Patched functions | 2,433 |
| Unique CVEs | 775 |
| Unique commits | 902 |
| Unique repositories | 362 |
| Mean function pairs per CVE | ≈ 3.1 |
| Max function pairs for a single CVE | 60 |

The CWE distribution is dominated by web-layer vulnerability classes typical of Java ecosystems: CWE-79 (XSS, 282 CVEs), CWE-611 (XXE, 210), CWE-22 (Path Traversal, 179), and CWE-89 (SQL Injection, 172) are the four most common categories.
CVSS base scores range from 1.2 to 10.0 (mean 6.27, median 6.5); 54% of entries carry a MEDIUM severity rating, 33% HIGH, 7% CRITICAL, and 7% LOW.
Sixty-two percent of entries use CVSSv2 scoring; the remaining 38% use CVSSv3.

### 3.2 Hyperparameter Configuration

We conduct a grid search over the following hyperparameters:

| Hyperparameter | Values searched |
|---|---|
| `mi_threshold` ($k$) | 50, 100 |
| `tabu_length` | 10, 50 |
| `max_indegree` | None, 3, 5 |

This yields 12 configurations (2 × 2 × 3); including an additional mi=200 sweep [TODO] the full grid is [TODO] configurations.
The MI threshold $k$ controls the number of features passed to structure learning: mi50 passes 51 variables (50 features + `is_vul`) and mi100 passes 101.
The HCS stopping criterion uses confidence parameter $\delta = 0.05$ and threshold $c = 0.10$, with a hard cap of 50 restarts.
For each configuration we run up to 50 HCS restarts and record the best BIC-d score, number of edges, distinct optima count, edges incident on `is_vul`, and wall-clock time.
ESS is set to 5 for all runs, placing a weak prior equivalent to 5 pseudo-observations spread uniformly over all parent configurations.

[TODO: ESS sensitivity check — post-hoc comparison of P(is_vul=1 | parents) at ESS ∈ {1, 5, 10, 20}.]

### 3.3 Implementation

The full pipeline is implemented in Python using pgmpy (Bayesian network learning and inference), scikit-learn (mutual information estimation), Spoon and GumTree via the Coming JAR (AST differencing), and pandas/pyarrow (data management).
The data extraction and Coming invocation steps run inside Docker (container `megavul-analysis`, `JAVA_OPTS=-Xmx4G`) to guarantee a consistent Java environment.
Downstream analysis (structure learning, parameter fitting, visualisation) runs outside Docker with `uv` for dependency management.
All random seeds are fixed (`random_state=0`) and pipeline snapshots are serialised to disk at each stage to ensure full reproducibility.
