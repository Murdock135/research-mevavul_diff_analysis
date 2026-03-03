# Research Paper — Todo

## BN1 (code changes → is_vul)

Script: `src/diff_analysis/scripts/megavul/bn1/visualize_bn1.py`
Outputs: `data/results/figures/bn1_f*.{png,pdf}`, `data/results/tables/bn1_t*.csv`

### Tables
- [x] **T1** — Dataset summary table (rows, balance, CVEs, repos, features, sparsity)
- [x] **T2** — Top-20 features by MI with `is_vul`
- [x] **T3** — BN structure summary (total edges, parent vs. child edges on `is_vul`)
- [x] **T4** — Lift table: P(is_vul=1 | each parent feature = 1, others = 0) via VariableElimination

### Figures
- [x] **F1** — DAG visualization: full (`bn1_f1_dag_full`) + ego graph (`bn1_f1_dag_ego`)
- [x] **F2** — P(is_vul=1) heatmap over all 8 combinations of the 3 binary parent states
- [x] **F3** — MI bar chart, top-20 features colored by action type (insert/delete/update/move-tree)

---

## BN2 (code changes → CWE)

- [ ] Train BN2: extract primary CWE from `cwe_ids`, learn structure, fit CPDs
- [ ] **T2b** — Top-20 features by MI with CWE
- [ ] **T3b** — BN structure summary for BN2
- [ ] **T4b** — Lift table for CWE node
- [ ] **F1b** — DAG visualization for BN2

---

## BN3 (code changes → CVSS severity)

- [ ] Train BN3: use `cvss_base_severity` as target, learn structure, fit CPDs
- [ ] **T2c** — Top-20 features by MI with severity
- [ ] **T3b** — BN structure summary for BN3
- [ ] **T4c** — Lift table for severity node
- [ ] **F1c** — DAG visualization for BN3

---

## Cross-BN (requires BN1 + BN2 + BN3)

- [ ] **F4** — Feature overlap across targets: UpSet plot or Venn diagram of parent feature sets
- [ ] **F5** — Side-by-side DAG comparison or shared-feature table

---

## Infrastructure / Misc

- [ ] Write `generate_figures.py` script under `src/diff_analysis/scripts/megavul/bn1/` for F1–F3
- [ ] Write `generate_tables.py` script under `src/diff_analysis/scripts/megavul/bn1/` for T1–T4
- [ ] Write `learn_dag_cwe.py` and `fit_bn2.py` under `bn2/`
- [ ] Write `learn_dag_severity.py` and `fit_bn3.py` under `bn3/`
- [ ] Decide on binarization strategy for CWE (top-N vs. one-vs-rest) and severity (4-class vs. binary HIGH/CRITICAL)
