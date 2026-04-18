# Methods

> **Status**: draft — placeholders marked `[TODO]` where results are pending.

---

## 2. Methods

### 2.1 AST-Diff Feature Extraction

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

### 2.2 Feature Selection via Mutual Information

The 496-feature space is too large for tractable Bayesian network structure learning; hill-climbing over hundreds of nodes faces an exponential search space and risks finding spurious edges among features with weak discriminative power.
We therefore apply a **mutual information (MI) pre-selection** step that retains only the top-$k$ features by MI with the target variable `is_vul`, reducing the input to structure learning from 496 to $k$ features.

MI is estimated using `sklearn.feature_selection.mutual_info_classif` with `discrete_features=True` (appropriate for binary count features) and a fixed `random_state=0` for reproducibility.

The top discriminating features are `delete-node_If_Block` and `insert-node_If_Block` (conditional-block additions/removals), followed by `delete-node_Invocation_Block` and `insert-node_Invocation_Block` (method-call changes inside blocks) — consistent with the intuition that security patches frequently add or restructure guard conditions.

### 2.3 Bayesian Network Structure Learning

We model the joint distribution of the MI-selected features and `is_vul` as a **discrete Bayesian network** (BN).
All feature columns and `is_vul` are treated as categorical (binary) variables.

#### Algorithm

Structure learning uses **Hill-Climb Search** (HCS) [CITE] as implemented in pgmpy, scored by the **discrete BIC** criterion (BIC-d):

$$\text{BIC-d} = \log P(\mathcal{D} \mid \hat{\theta}, \mathcal{G}) - \frac{\log N}{2} |\mathcal{G}|$$

where $|\mathcal{G}|$ is the number of free parameters in the model and $N$ is the sample size.
BIC-d penalises model complexity relative to data fit, which is appropriate for the high-dimensional sparse feature matrix where overfitting is the primary risk.

#### HCS random restarts

Hill-climbing is sensitive to local optima.
To characterise the search landscape and select the globally best structure, we employ the **High-Confidence Stopping (HCS) rule** of Dick, Wong, and Dann [CITE].
The rule is grounded in the concept of *missing mass* $M_n$ — the total probability mass of all local optima that have not yet been observed after $n$ restarts.
Using the Good-Turing estimator $G_n = |F^1_n|/n$, where $|F^1_n|$ is the count of distinct local optima (identified by their edge sets) seen exactly once so far, McAllester and Schapire (2000) prove that with probability at least $1 - \delta$:

$$M_n \leq G_n + (2\sqrt{2} + \sqrt{3}) \sqrt{\frac{\ln(3/\delta)}{n}}$$

The HCS rule stops after restart $n$ when this upper bound $C_n$ falls below a threshold $c$:

$$C_n = \frac{|F^1_n|}{n} + (2\sqrt{2} + \sqrt{3}) \sqrt{\frac{\ln(3/\delta)}{n}} < c$$

At each restart, a random DAG with $\lfloor N_{\text{nodes}} / 4 \rfloor$ edges is drawn as the starting point, and a full hill-climb run is executed from it.
The confidence parameter $\delta$ and threshold $c$, along with a hard cap on the number of restarts, are specified in §3.2.
The best-scoring DAG across all restarts is returned as the final structure.

### 2.4 Parameter Estimation

Conditional probability distributions (CPDs) are fitted to the learned DAG using the **BDeu** (Bayesian Dirichlet equivalent uniform) estimator [CITE]:

$$P(\theta \mid \mathcal{D}, \mathcal{G}) \propto P(\mathcal{D} \mid \theta, \mathcal{G}) \cdot \text{Dir}(\alpha / (q_i \cdot r_i))$$

where $\alpha$ is the **equivalent sample size** (ESS), $q_i$ is the number of parent configurations for node $i$, and $r_i$ is its cardinality.
A small ESS regularises CPDs for rare parent combinations (which are frequent given the dataset sparsity) while having minimal influence on well-supported configurations.

### 2.5 Inference

Probabilistic queries over the fitted BN are answered using **Variable Elimination** as implemented in pgmpy.
We report **lift** — the ratio $P(\text{is\_vul}=1 \mid X_i=1) / P(\text{is\_vul}=1)$ — for each direct parent of `is_vul`, holding all other parents at 0, to quantify the marginal predictive contribution of each feature.
