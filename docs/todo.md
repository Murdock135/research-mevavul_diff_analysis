# Research Paper — Todo

## BN1 (code changes → is_vul)

Script: `analysis/bn1/visualize_bn1.py`
Outputs: `data/results/figures/bn1/`, `data/results/tables/bn1/`

### Methodology (HCS + grid search)
- [x] Implement HCS random restarts in `bn/pipeline.py` (store `hcs_history`, `edge_inclusion` on pipeline)
- [x] Add HCS CLI args to `learn_dag_isvul.py`
- [x] Create `tune_bn1.py` grid search (27 configs × HCS adaptive restarts)
- [ ] Re-run structure learning + fitting with best config from grid search
- [ ] ESS sensitivity check: post-hoc script fitting best-config pipeline at ESS ∈ {1, 5, 10, 20}; compare P(is_vul=1 | parents) and lift ranking across values

### Tables
- [x] **T1** — Dataset summary (rows, balance, CVEs, repos, features, sparsity)
- [x] **T2** — Top-20 features by MI with `is_vul`
- [x] **T3** — BN structure summary (edges, parents/children on `is_vul`) — *update with HCS stats: n_restarts, n_distinct_optima, final BIC score*
- [x] **T4** — Lift table: P(is_vul=1 | each parent=1, others=0) via VariableElimination
- [ ] **T5** — Hyperparameter grid search results (`tune_bn1_grid.csv`); all 27 configs ranked by BIC-d

### Figures
- [x] **F1** — DAG visualization: full DAG + ego graph — *regenerate with best-config pipeline*
- [x] **F2** — P(is_vul=1) heatmap over all 8 parent state combinations
- [x] **F3** — MI bar chart, top-20 features colored by action type
- [x] **F4** — HCS convergence curve: `cn` vs. restart `n`, dashed line at threshold `c`; one curve per run
- [ ] **F5** — Edge stability / inclusion frequency: bar chart of edges appearing in ≥1 restart, colored by action type, edges incident on `is_vul` highlighted

---

## BN2 (code changes → CWE)

- [ ] Decide encoding: top-N multi-class vs. one-vs-rest binary per CWE
- [ ] Train BN2: extract primary CWE, learn structure (HCS), fit CPDs
- [ ] **T2b** — Top-20 features by MI with CWE
- [ ] **T3b** — BN structure summary for BN2
- [ ] **T4b** — Lift table for CWE node
- [ ] **T5b** — Grid search results for BN2
- [ ] **F1b** — DAG visualization for BN2
- [ ] **F4b** — HCS convergence curve for BN2
- [ ] **F5b** — Edge stability for BN2

---

## BN3 (code changes → CVSS severity)

- [ ] Decide encoding: 4-class vs. binary HIGH/CRITICAL
- [ ] Train BN3: use `cvss_base_severity`, learn structure (HCS), fit CPDs
- [ ] **T2c** — Top-20 features by MI with severity
- [ ] **T3c** — BN structure summary for BN3
- [ ] **T4c** — Lift table for severity node
- [ ] **T5c** — Grid search results for BN3
- [ ] **F1c** — DAG visualization for BN3
- [ ] **F4c** — HCS convergence curve for BN3
- [ ] **F5c** — Edge stability for BN3

---

## Cross-BN (requires BN1 + BN2 + BN3)

- [ ] **F6** — Feature overlap across targets: UpSet plot or Venn diagram of parent feature sets
- [ ] **F7** — Side-by-side edge stability comparison across BN1/BN2/BN3

---

## Evaluation

### Primary: fixed-structure 5-fold CV (within MegaVul)
- [ ] Implement 5-fold CV over CPD parameters with DAG structure fixed (no re-running Coming or structure learning)
  - Split 4,866 samples into 5 folds; for each fold, call `BayesianNetwork.fit()` on 4 folds, predict `is_vul` via Variable Elimination on the held-out fold
  - Report AUC, F1, accuracy averaged across folds
  - **Note**: structure was learned on all data; this measures parameter generalisation only. Acknowledge MI leakage in paper: *"Feature selection was performed on the full dataset prior to evaluation; predictive performance estimates may be optimistic."*
  - Compare against logistic regression and naive Bayes baselines on same folds

### External validity: SARD
- [ ] Download NIST SARD Java samples (Juliet test suite); filter to CWEs overlapping MegaVul top-4 (CWE-79, CWE-611, CWE-22, CWE-89)
- [ ] Construct (bad, good) function pairs using the same pair-directory format as MegaVul (`02_create_pairs.py` or equivalent); apply same class-wrapping
- [ ] Run Coming on SARD pairs inside Docker (same `03_get_diffs.py` pipeline)
- [ ] Build feature matrix for SARD samples; apply same 70-feature vocabulary from MegaVul MI selection
- [ ] Predict `is_vul` using fitted MegaVul BN; report AUC, F1
- [ ] **Note**: SARD Juliet samples are synthetic/algorithmically generated — expect distribution shift from real CVE patches; frame as external validity experiment, not primary evaluation
- [ ] Check CWE coverage and parse failure rate for SARD samples

---

## Infrastructure / Misc

- [ ] Write `learn_dag_cwe.py` and `fit_bn2.py` under `bn2/`
- [ ] Write `learn_dag_severity.py` and `fit_bn3.py` under `bn3/`
- [ ] Shared `visualize_bn.py` base (or parameterize `visualize_bn1.py`) for BN2/BN3 reuse
- [ ] Add docstrings to all public classes and methods (`MegaVulExtractor`, `MegaVulDataPreparator`, `ComingTool`, `BNPipeline`, `ExperimentTracker`, types); pick and configure a doc generation tool (e.g. `pdoc` or `mkdocs`)
