# BN1 Hyperparameters

Hyperparameters for structure learning (`learn_dag_isvul.py`) and parameter fitting
(`fit_bn1.py`) of BN1 — the Bayesian network modelling code-change features as
predictors of `is_vul`.

---

## 1. Data & Preprocessing

| Parameter | Value | Description |
|---|---|---|
| **Dataset** | MegaVul | Java functions; CVE-linked vulnerable / patched pairs |
| **Feature matrix** | `data/processed/feature_matrix.parquet` | 496 binary AST-diff features × 4 866 samples |
| **Target variable** | `is_vul` | Binary: 1 = vulnerable function, 0 = patched function |
| **`feature_threshold`** | `0.0001` | Drop features non-zero in fewer than 0.01 % of samples; retains features present in ≥ 1 sample |
| **`sample_threshold`** | `0.0` | No sample filtering (all 4 866 rows retained) |
| **`mi_threshold`** | `50` or `100` | Retain only the top-N features by mutual information with `is_vul` before structure learning |
| **Dtype cast** | `.astype(str)` | Applied after all numeric filtering; forces pgmpy to infer columns as categorical (`'C'`) not numerical (`'N'`) |

---

## 2. MI Pre-selection

Mutual information is computed via `sklearn.feature_selection.mutual_info_classif`
with `discrete_features=True` and `random_state=0`.

| Run | `mi_threshold` | Features entering structure learning |
|---|---|---|
| **mi50** | 50 | 50 features + `is_vul` → model\_df shape **(4 866, 51)** |
| **mi100** | 100 | 100 features + `is_vul` → model\_df shape **(4 866, 101)** |

---

## 3. Structure Learning — HillClimbSearch

Algorithm: **Hill-Climb Search** (`pgmpy.estimators.HillClimbSearch`)

| Parameter | Value | Notes |
|---|---|---|
| **`method`** | `hillclimb` | Greedy hill-climbing with tabu list |
| **`scoring_method`** | `bic-d` | Discrete BIC (Bayesian Information Criterion) |
| **`tabu_length`** | `10` | Number of recently-reversed moves blocked at each step |
| **`max_iter`** | `1000` | Maximum iterations; both runs converged before the limit |
| **`max_indegree`** | `None` | No cap on the number of parents per node |
| **`significance_level`** | N/A | MMHC-only; not used |
| **`ci_test`** | N/A | MMHC-only; not used |

### Convergence

Both runs terminated early when no improving move remained.

| Run | Edges learned | Edges incident on `is_vul` |
|---|---|---|
| **mi50** | 70 | 3 parents + 26 children |
| **mi100** | 118 | 1 parent + 29 children (3 parents via shared edges) |

> HillClimbSearch returns non-zero exit before `max_iter` when the score
> cannot be improved by any single add / remove / reverse operation. This is
> the normal and expected termination condition, not an error.

---

## 4. Parameter Fitting — BayesianEstimator

Algorithm: **BDeu** (Bayesian Dirichlet equivalent uniform) prior
(`pgmpy.estimators.BayesianEstimator`)

| Parameter | Value | Notes |
|---|---|---|
| **`estimator`** | `BayesianEstimator` | Bayesian parameter estimation with Dirichlet prior |
| **`prior_type`** | `BDeu` | Equivalent uniform prior over all DAG structures |
| **`equivalent_sample_size`** | `5.0` | ESS controls prior strength; higher = smoother CPDs, less data-sensitive |

The fitted CPDs are saved alongside the model:

- `data/results/<stem>_fitted.pkl` — pickled `DiscreteBayesianNetwork`
- `data/results/<stem>_cpd_is_vul.txt` — CPD table for the `is_vul` node

---

## 5. Output Files

| File | Description |
|---|---|
| `bn1_hillclimb_mi50_edges.{csv,json}` | Learned edge list (mi50 run) |
| `bn1_hillclimb_mi50_pipeline.pkl` | Preprocessed `model_df` + edges snapshot |
| `bn1_hillclimb_mi50_fitted.pkl` | Fitted `DiscreteBayesianNetwork` |
| `bn1_hillclimb_mi50_cpd_is_vul.txt` | CPD table for `is_vul` |
| `bn1_hillclimb_mi100_*` | Same set for the mi100 run |

---

## 6. Rationale for Key Choices

**BIC-d scoring** — penalises model complexity (number of edges) relative to
data fit; appropriate for the sparse, high-dimensional binary feature matrix
where overfitting is the main risk.

**BDeu prior with ESS = 5** — a weak prior (equivalent to 5 pseudo-observations
spread uniformly over parent configurations) that regularises CPDs for rare
parent combinations while having minimal influence on well-supported ones.

**MI pre-selection** — reduces the search space from 496 features to 50 / 100
before structure learning. The MI filter is label-guided (w.r.t. `is_vul`) and
avoids the exponential blow-up of HillClimbSearch over hundreds of variables.
`random_state=0` is fixed for reproducibility.

**`discrete_features=True`** in MI estimation — all features are binary counts
(0 / 1), so the discrete MI estimator (based on frequency counts, not KDE) is
the correct choice.
