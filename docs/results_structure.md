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

---

## Paper-ready run bundles (`data/results/paper/`)

This directory contains MI-specific experiment bundles used for paper analysis and
reporting. The layout is stable across MI thresholds.

```
data/results/paper/
├── experiment.json                       top-level run metadata/index
├── mi50/
│   ├── experiment.json                   experiment metadata for MI=50 bundle
│   ├── summary.csv                       one row per config, tabular summary
│   ├── summary.jsonl                     one JSON object per config
│   └── configs/
│       ├── 001_mi50_tabu10_indegNone/
│       ├── ...
│       └── 008_mi50_tabu100_indeg3/
└── mi100/
    ├── experiment.json                   experiment metadata for MI=100 bundle
    ├── summary.csv                       one row per config, tabular summary
    ├── summary.jsonl                     one JSON object per config
    └── configs/
        ├── 001_mi100_tabu10_indegNone/
        ├── ...
        └── 009_mi100_tabu100_indeg5/
```

Each config directory is flat and contains:

```
data/results/paper/mi{N}/configs/{NNN}_{slug}/
├── config.json                           config_index, mi_threshold,
│                                         tabu_length, max_indegree, started_at
├── result.json                           aggregate outcome for that config:
│                                         ended_at, n_restarts_used,
│                                         n_distinct_optima, n_edges, bic_score,
│                                         n_edges_on_target, elapsed_s, error
└── hcs_restarts.jsonl                    one JSON line per restart:
                                          restart, score, f1, cn, hcs_c, edges
```

In the current observed bundles, `hcs_restarts.jsonl` has 50 lines per config
(one line per restart).

### File structure: `paper/experiment.json` (top-level)

Observed keys:

- `script` (string): producer script path/module
- `description` (string): free-text run summary
- `runs` (array): one object per bundled MI sub-run
- `hcs_params` (object): shared HCS settings
- `fixed_params` (object): shared fixed tuning params
- `max_restarts_per_config` (int)
- `total_configs_completed` (int)

`runs[]` object keys (observed):

- `dir` (string): subdirectory name (for example, `mi50`, `mi100`)
- `source_timestamp` (string): original timestamped run id
- `started_at` (ISO timestamp string)
- `n_workers` (int, optional)
- `grid` (object): parameter grid used for that run
- `n_configs_completed` (int)
- `note` (string, optional)

`hcs_params` keys:

- `delta` (float)
- `c` (float)
- `max_restarts` (int)

`fixed_params` keys:

- `scoring` (string)
- `max_iter` (int)
- `feature_threshold` (float)
- `sample_threshold` (float)
- `target_col` (string)

### File structure: `paper/mi{N}/experiment.json`

Observed keys:

- `started_at` (ISO timestamp string)
- `script` (string)
- `n_workers` (int)
- `grid` (object): parameter grid object
- `grid.mi_threshold` (array[int])
- `grid.tabu_length` (array[int])
- `grid.max_indegree` (array[int|null])
- `hcs_params` (object): `delta`, `c`, `max_restarts`
- `fixed_params` (object): `scoring`, `max_iter`, `feature_threshold`,
    `sample_threshold`, `target_col`
- `n_configs` (int): planned config count from grid
- `max_restarts_per_config` (int)
- `max_total_restarts` (int)

### File structure: `paper/mi{N}/configs/{NNN}_{slug}/config.json`

Observed keys:

- `config_index` (int)
- `mi_threshold` (int)
- `tabu_length` (int)
- `max_indegree` (int|null)
- `started_at` (ISO timestamp string)

### File structure: `paper/mi{N}/configs/{NNN}_{slug}/result.json`

Observed keys:

- `config_index` (int)
- `mi_threshold` (int)
- `tabu_length` (int)
- `max_indegree` (int|null)
- `started_at` (ISO timestamp string)
- `ended_at` (ISO timestamp string)
- `n_restarts_used` (int)
- `n_distinct_optima` (int)
- `n_edges` (int)
- `bic_score` (float)
- `n_edges_on_target` (int)
- `elapsed_s` (float)
- `error` (string|null)

### File structure: `paper/mi{N}/configs/{NNN}_{slug}/hcs_restarts.jsonl`

One JSON object per line. Observed keys per line:

- `restart` (int)
- `score` (float)
- `f1` (int)
- `cn` (float)
- `hcs_c` (float)
- `edges` (array[[string, string]]): directed edges as `[source, target]`

### File structure: `paper/mi{N}/summary.csv` and `summary.jsonl`

Both encode one record per config; `summary.jsonl` uses the same fields as rows in
`summary.csv`.

Observed columns/keys:

- `config_index`
- `mi_threshold`
- `tabu_length`
- `max_indegree`
- `started_at`
- `ended_at`
- `n_restarts_used`
- `n_distinct_optima`
- `n_edges`
- `bic_score`
- `n_edges_on_target`
- `elapsed_s`
- `error`
