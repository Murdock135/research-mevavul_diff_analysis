# Results Directory Structure

All outputs are under `data/results/` (gitignored).

---

## BN1 — Structure learning & fitting

Produced by `learn_dag_isvul.py` and `fit_bn1.py`.
Stem format: `bn1_{method}[_mi{N}]_hcs`

```
data/results/
├── {stem}_edges.csv          edge list (source, target)
├── {stem}_edges.json         edge list (JSON)
├── {stem}_pipeline.pkl       BNPipeline snapshot — preprocessed model_df,
│                             learned edges, hcs_history, edge_inclusion
└── {stem}_fitted.pkl         fitted DiscreteBayesianNetwork (CPDs)
```

---

## BN1 — Figures & tables

Produced by `visualize_bn1.py`.

```
data/results/
├── figures/bn1/
│   ├── dag_full/             F1 — full DAG (.png + .pdf)
│   ├── dag_ego/              F1 — ego graph around is_vul (.png + .pdf)
│   ├── heatmap/              F2 — P(is_vul=1) heatmap (.png + .pdf)
│   ├── mi_bar/               F3 — MI bar chart, top-20 features (.png + .pdf)
│   ├── hcs_convergence/      F4 — cn vs. restart curve (.png + .pdf)
│   └── edge_stability/       F5 — edge inclusion frequency bar chart (.png + .pdf)
└── tables/bn1/
    ├── dataset/              T1 — dataset summary
    ├── mi_top20/             T2 — top-20 features by MI
    ├── structure/            T3 — BN structure summary (edges, HCS stats)
    ├── lift/                 T4 — lift table P(is_vul=1 | each parent)
    └── grid/                 T5 — hyperparameter grid search results
```

All figure files follow the naming convention `{stem}_{figure_id}.{ext}`.

---

## BN1 — Hyperparameter grid search

Produced by `tune_bn1.py` via `ExperimentTracker`.
One timestamped subdirectory per invocation — multiple runs never overwrite each other.

```
data/results/tune_bn1/
└── {YYYYMMDD_HHMMSS}/                    experiment invocation timestamp
    ├── experiment.json                   full experiment config written upfront:
    │                                     grid, HCS params, fixed params (scoring,
    │                                     max_iter, thresholds, target_col),
    │                                     n_configs, max_restarts_per_config
    ├── configs/
    │   ├── {NNN}_{slug}/                 NNN = 1-indexed, slug = mi{m}_tabu{t}_indeg{d}
    │   │   ├── config.json               hyperparams for this config + started_at
    │   │   ├── result.json               bic_score, n_edges, n_restarts_used,
    │   │   │                             n_distinct_optima, n_edges_on_target,
    │   │   │                             elapsed_s, started_at, ended_at, error
    │   │   └── hcs_restarts.jsonl        one JSON line per HCS restart:
    │   │                                 restart, score, f1, cn, hcs_c, edges
    │   └── ...
    ├── summary.csv                       all configs, one row each;
    │                                     appended incrementally — crash-safe;
    │                                     re-sorted by bic_score desc at end
    └── summary.jsonl                     same content, JSONL format
```
