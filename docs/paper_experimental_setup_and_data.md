# Methods and Experimental Setup

> **Status**: draft — placeholders marked `[TODO]` where results are pending.

---

## 3. Methods

### 3.1 AST-Diff Feature Extraction

We apply Coming [CITE] to each function pair to extract structural change features (see §II-B for tool background).
Each action is qualified by the type of the affected node and its parent context — e.g., `insert-node_If_Block` (an `If` node inserted inside a `Block`) or `delete-node_Invocation_Block` (a method call removed from a `Block`).
Of Coming's five action types, permutation was never observed in our dataset; the effective feature space covers insert-node, delete-node, update-node, and move-tree.

We compute diffs in the **bug-fixing direction** (vulnerable → patched) to obtain features describing how the patch modifies the AST.
To produce mirrored rows for the vulnerable side without running Coming twice, we also compute diffs in the **bug-inducing direction** (patched → vulnerable), which yields the complementary edit script.
The feature value for each `(sample, feature)` cell is the count of that action type in the diff; because most security patches are small, these counts are almost always 0 or 1, making the features effectively binary in practice.

Because Coming requires a compilable Java file, bare function snippets are wrapped in a synthetic class declaration (`class <func_name> { ... }`) before being written to disk, allowing Spoon to produce non-empty ASTs.

### 3.2 Feature Selection via Mutual Information

The 496-feature space is too large for tractable BN structure learning; hill-climbing over hundreds of nodes faces an exponential search space and risks spurious edges among weakly discriminative features.
We therefore apply a **mutual information (MI) pre-selection** step that retains only the top-$k$ features by MI with the target variable `is_vul`, reducing the structure-learning input from 496 to $k$ features.

MI is estimated using `sklearn.feature_selection.mutual_info_classif` with `discrete_features=True` (appropriate for binary count features) and `random_state=0` for reproducibility.

### 3.3 Bayesian Network Structure Learning

We model the joint distribution of the MI-selected features and `is_vul` as a discrete BN, treating all variables as binary.
Structure learning uses Hill-Climb Search scored by BIC-d (see §II-A for background on score-based structure learning).

#### HCS random restarts

Because hill-climbing is sensitive to local optima, we employ the **random-restart strategy** of Dann, Dick & Wong [CITE], which uses a Good-Turing estimator to bound the probability mass held by still-undiscovered local optima.

At each restart $n$, a random DAG with $\lfloor N_{\text{nodes}} / 4 \rfloor$ edges is drawn as the starting point and a full hill-climb run is executed from it.
After each restart, let $f_1$ denote the number of distinct local optima (identified by their edge sets) seen exactly once so far.
The stopping criterion is:

$$c_n = \frac{f_1}{n} + (2\sqrt{2} + \sqrt{3}) \sqrt{\frac{\log(3/\delta)}{n}} < c$$

with confidence parameter $\delta = 0.05$ and threshold $c = 0.10$.
When $c_n < c$ the procedure terminates; the highest-BIC-d DAG across all restarts is returned as the final structure.

### 3.4 Parameter Estimation

CPDs are fitted to the learned DAG using the **BDeu** estimator [CITE] with equivalent sample size ESS = 5, placing a weak prior of 5 pseudo-observations spread uniformly over all parent configurations.
This regularises CPDs for rare parent combinations — frequent given dataset sparsity — while having minimal influence on well-supported configurations.

### 3.5 Inference

Probabilistic queries are answered using **Variable Elimination** as implemented in pgmpy.
We report **lift** — $P(\text{is\_vul}=1 \mid X_i=1) \;/\; P(\text{is\_vul}=1)$ — for each direct parent of `is_vul`, holding all other parents at 0, to quantify the marginal predictive contribution of each feature.

---

## 4. Experimental Setup

### 4.1 Dataset

We use the **MegaVul** dataset [CITE], which provides CVE-linked pairs of vulnerable and patched Java functions extracted from open-source GitHub repositories.
Each entry links a CVE to one or more security-fixing commits; for each commit, functions that were modified are recorded as `(func_before, func_after)` pairs together with CVE metadata (CWE identifiers, CVSS score and severity, commit hash, repository URL, and commit message).

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

#### Feature matrix

The extraction pipeline produces a feature matrix of shape **4,866 × 496**, where the 496 columns correspond to distinct action–node–context triples observed across the dataset (137 `delete-node`, 136 `insert-node`, 93 `update-node`, and 130 `move-tree` features).
The matrix is extremely sparse: 97.8% of features have more than 95% zero values, and 86.1% exceed 99% zeros.
No feature is entirely zero — every operation type was observed at least once.

The median total number of AST operations per function is 2 (mean 2.86).
303 samples (6.2%) have no recorded AST operations, likely due to Spoon parse failures on unusual Java constructs or whitespace-only diffs that yield no structural edits; these are retained but contribute no signal to structure learning.

### 4.2 Hyperparameter Configuration

We conduct a grid search over three hyperparameters:

| Hyperparameter | Values |
|---|---|
| `mi_threshold` ($k$) | 50, 100 |
| `tabu_length` | 10, 50 |
| `max_indegree` | None, 3, 5 |

This yields 12 configurations (2 × 2 × 3); including an additional mi=200 sweep [TODO] the full grid is [TODO] configurations.
For each configuration we run up to 50 HCS restarts (hard cap) and record the best BIC-d score, edge count, distinct optima count, edges incident on `is_vul`, and wall-clock time.

The top discriminating MI features are `delete-node_If_Block` and `insert-node_If_Block` (conditional-block additions/removals), followed by `delete-node_Invocation_Block` and `insert-node_Invocation_Block` (method-call changes inside blocks) — consistent with the intuition that security patches frequently add or restructure guard conditions.
We evaluate two MI thresholds, referred to as **mi50** ($k=50$, passing 51 variables to structure learning) and **mi100** ($k=100$, passing 101 variables).

Grid search results show that at mi=50, BIC-d scores are nearly identical across all tabu/indegree configurations (−24,095.95 to −24,096) and edge counts are stable at 69–70.
HCS hit the 50-restart cap on every configuration (45 of 50 restarts found distinct local optima for mi50), indicating a highly multimodal search landscape.
The `max_indegree` constraint never binds — the unconstrained solution naturally has at most 3 parents per node (mean degree ≈ 1.35) — so it is fixed to `None` in all reported models.
The tabu list length is set to 10 and the maximum iteration count to 1,000; both mi50 and mi100 runs converge early well before this limit.

[TODO: ESS sensitivity check — post-hoc comparison of P(is_vul=1 | parents) at ESS ∈ {1, 5, 10, 20}.]

### 4.3 Implementation

The full pipeline is implemented in Python using pgmpy (BN learning and inference), scikit-learn (MI estimation), Spoon and GumTree via the Coming JAR (AST differencing), and pandas/pyarrow (data management).
The data extraction and Coming invocation steps run inside Docker (container `megavul-analysis`, `JAVA_OPTS=-Xmx4G`) to guarantee a consistent Java environment.
Downstream analysis runs outside Docker with `uv` for dependency management.
All random seeds are fixed (`random_state=0`) and pipeline snapshots are serialised to disk at each stage to ensure full reproducibility.
