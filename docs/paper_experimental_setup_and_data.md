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

### 3.2 AST-Diff Feature Extraction

Raw source code is not consumed directly.
Instead, we extract structural change features from the Abstract Syntax Trees (ASTs) of each function pair using **Coming** [CITE], a tool built on the Spoon Java parser and the GumTree AST differencing algorithm.
Coming computes a fine-grained edit script between the ASTs of the before and after versions of each function, decomposing the diff into four atomic edit actions:

- **insert-node** — a new AST node is added in the patched version;
- **delete-node** — an existing AST node is removed;
- **update-node** — the value of an AST node is changed in place;
- **move-tree** — a subtree is relocated to a different position in the AST.

Each action is qualified by the type of the affected node and the type of its parent context, giving features of the form `<action>_<NodeType>_<ParentContext>` (e.g., `insert-node_If_Block`, `delete-node_Invocation_Block`).
We compute diffs in the **bug-fixing direction** (vulnerable → patched) to obtain features that describe how the patch modifies the AST.
To produce mirrored rows for the vulnerable side without running Coming twice, we also compute diffs in the **bug-inducing direction** (patched → vulnerable), which yields the complementary edit script.
The feature value for each `(sample, feature)` cell is the **count** of that edit action type in the corresponding diff; because most security patches are small, these counts are almost always 0 or 1, making the features effectively binary in practice.

Because Coming requires a compilable Java file, bare function snippets are wrapped in a synthetic class declaration (`class <func_name> { ... }`) before being written to disk, allowing Spoon to produce non-empty ASTs.

#### Feature matrix

The extraction pipeline produces a feature matrix of shape **4,866 × 496**, where the 496 columns correspond to distinct `<action>_<NodeType>_<ParentContext>` triples observed across the dataset (137 `delete-node`, 136 `insert-node`, 93 `update-node`, and 130 `move-tree` features).
The matrix is extremely sparse: 97.8% of features have more than 95% zero values, and 86.1% exceed 99% zeros.
No feature is entirely zero — every operation type was observed at least once.

The median total number of AST operations per function is 2, and the mean is 2.86.
303 samples (6.2%) have no recorded AST operations, likely due to Spoon parse failures on unusual Java constructs or to trivially whitespace-only diffs that yield no structural edits; these samples are retained in the dataset but contribute no signal to structure learning.

### 3.3 Feature Selection via Mutual Information

The 496-feature space is too large for tractable Bayesian network structure learning; hill-climbing over hundreds of nodes faces an exponential search space and risks finding spurious edges among features with weak discriminative power.
We therefore apply a **mutual information (MI) pre-selection** step that retains only the top-$k$ features by MI with the target variable `is_vul`, reducing the input to structure learning from 496 to $k$ features.

MI is estimated using `sklearn.feature_selection.mutual_info_classif` with `discrete_features=True` (appropriate for binary count features) and a fixed `random_state=0` for reproducibility.
We evaluate two thresholds in our experiments: $k \in \{50, 100\}$, referred to as **mi50** and **mi100**.
The mi50 setting passes 51 variables (50 features + `is_vul`) to structure learning; mi100 passes 101.

The top discriminating features are `delete-node_If_Block` and `insert-node_If_Block` (conditional-block additions/removals), followed by `delete-node_Invocation_Block` and `insert-node_Invocation_Block` (method-call changes inside blocks) — consistent with the intuition that security patches frequently add or restructure guard conditions.

### 3.4 Bayesian Network Structure Learning

We model the joint distribution of the MI-selected features and `is_vul` as a **discrete Bayesian network** (BN).
All feature columns and `is_vul` are treated as categorical (binary) variables.

#### Algorithm

Structure learning uses **Hill-Climb Search** (HCS) [CITE] as implemented in pgmpy, scored by the **discrete BIC** criterion (BIC-d):

$$\text{BIC-d} = \log P(\mathcal{D} \mid \hat{\theta}, \mathcal{G}) - \frac{\log N}{2} |\mathcal{G}|$$

where $|\mathcal{G}|$ is the number of free parameters in the model and $N$ is the sample size.
BIC-d penalises model complexity relative to data fit, which is appropriate for the high-dimensional sparse feature matrix where overfitting is the primary risk.
The tabu list length is set to 10 (preventing recently reversed moves from being immediately re-applied), and the maximum iteration count is 1,000; both mi50 and mi100 runs converge early (no improving single-edge move remains) well before this limit.

#### HCS random restarts

Hill-climbing is sensitive to local optima.
To characterise the search landscape and select the globally best structure, we employ the **HCS random-restart strategy** of Dann, Dick & Wong [CITE], which uses a Good-Turing estimator to bound the probability mass held by still-undiscovered local optima.

At each restart $n$, a random DAG with $\lfloor N_{\text{nodes}} / 4 \rfloor$ edges is drawn as the starting point, and a full hill-climb run is executed from it.
After each restart, let $f_1$ denote the number of distinct local optima (identified by their edge sets) seen exactly once so far.
The stopping criterion is:

$$c_n = \frac{f_1}{n} + (2\sqrt{2} + \sqrt{3}) \sqrt{\frac{\log(3/\delta)}{n}} < c$$

with confidence parameter $\delta = 0.05$ and threshold $c = 0.10$.
When $c_n < c$ the procedure terminates and returns the highest-BIC-d DAG observed across all restarts; a hard cap of 50 restarts is enforced as a safety valve.

In our grid search runs, HCS hit the 50-restart cap on every configuration (45 of 50 restarts found distinct local optima for mi50), indicating the search landscape is highly multimodal and that 50 restarts are insufficient to satisfy the stopping criterion.
The best-scoring DAG across all restarts is taken as the final structure.

#### Hyperparameter grid search

We conduct a grid search over three hyperparameters:

| Hyperparameter | Values |
|---|---|
| `mi_threshold` ($k$) | 50, 100 |
| `tabu_length` | 10, 50 |
| `max_indegree` | None, 3, 5 |

This yields 12 configurations (2 × 2 × 3); including an additional mi=200 sweep [TODO] the full grid is [TODO] configurations.
For each configuration we run up to 50 HCS restarts and record the best BIC-d score, number of edges, distinct optima count, edges incident on `is_vul`, and wall-clock time.

Grid search results show that at mi=50, BIC-d scores are nearly identical across all tabu/indegree configurations (−24,095.95 to −24,096), and edge counts are stable at 69–70 edges.
The `max_indegree` constraint never binds because the unconstrained hill-climb solution naturally has at most 3 parents per node (mean degree ≈ 1.35); this parameter is therefore fixed to `None` in all reported models.

### 3.5 Parameter Estimation

Conditional probability distributions (CPDs) are fitted to the learned DAG using the **BDeu** (Bayesian Dirichlet equivalent uniform) estimator [CITE]:

$$P(\theta \mid \mathcal{D}, \mathcal{G}) \propto P(\mathcal{D} \mid \theta, \mathcal{G}) \cdot \text{Dir}(\alpha / (q_i \cdot r_i))$$

where $\alpha$ is the **equivalent sample size** (ESS), $q_i$ is the number of parent configurations for node $i$, and $r_i$ is its cardinality.
We set ESS = 5, placing a weak prior equivalent to 5 pseudo-observations spread uniformly over all parent configurations.
This regularises CPDs for rare parent combinations (which are frequent given the dataset sparsity) while having minimal influence on well-supported configurations.

[TODO: ESS sensitivity check — post-hoc comparison of P(is_vul=1 | parents) at ESS ∈ {1, 5, 10, 20}.]

### 3.6 Inference

Probabilistic queries over the fitted BN are answered using **Variable Elimination** as implemented in pgmpy.
We report **lift** — the ratio $P(\text{is\_vul}=1 \mid X_i=1) / P(\text{is\_vul}=1)$ — for each direct parent of `is_vul`, holding all other parents at 0, to quantify the marginal predictive contribution of each feature.

### 3.7 Implementation

The full pipeline is implemented in Python using pgmpy (Bayesian network learning and inference), scikit-learn (mutual information estimation), Spoon and GumTree via the Coming JAR (AST differencing), and pandas/pyarrow (data management).
The data extraction and Coming invocation steps run inside Docker (container `megavul-analysis`, `JAVA_OPTS=-Xmx4G`) to guarantee a consistent Java environment.
Downstream analysis (structure learning, parameter fitting, visualisation) runs outside Docker with `uv` for dependency management.
All random seeds are fixed (`random_state=0`) and pipeline snapshots are serialised to disk at each stage to ensure full reproducibility.
