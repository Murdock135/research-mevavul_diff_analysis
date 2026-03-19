# Metrics Used In notebooks/plots.ipynb

This table documents each metric used in the notebook and what it means.

## Scope Legend

- Single restart run: one hill-climb restart execution within a config.
- Single config: one hyperparameter configuration (for example, one tabu/indegree setting).
- Pair of configs: one comparison between two configs in the same experiment.
- Whole experiment: all configs under one MI feature-selection setting directory (for example, mi50).
- One summary row: one row loaded from summary.csv.
- MI setting aggregate: values aggregated over all summary rows for one MI feature-selection setting (mi50 or mi100).
- Input field: a source field used to derive another reported metric.

| Metric | Aggregation level | Meaning |
|---|---|---|
| score | Single restart run | Objective value for a single restart (BIC-style objective in this workflow). Higher is better. |
| restart | Single restart run | Restart identifier/index for a run (identifier, not a quality metric). |
| edges | Single restart run | Directed edge set of the learned DAG for a restart. Used as input to SHD computation. |
| best_score | Single config | Highest score among restarts for a given configuration. |
| best_restart | Single config | Restart identifier/index that achieved `best_score`. |
| mean_shd_to_best | Single config | Mean SHD between each non-best restart DAG and the best DAG within the same config. Lower means better restart stability. |
| var_shd_to_best | Single config | Variance of SHD-to-best values across restarts in a config. |
| min_shd_to_best | Single config | Smallest SHD-to-best among non-best restarts (closest alternative structure). |
| max_shd_to_best | Single config | Largest SHD-to-best among non-best restarts (most different alternative structure). |
| n_restarts | Single config or MI setting aggregate | Number of restarts used. At config level this is per config; in MI setting summary it is the representative value from summary data. |
| shd | Pair of configs | Directed Structural Hamming Distance between two best DAGs from two configs in the same experiment. Counts disagreements in edge state (no edge vs u->v vs v->u). |
| mean_shd | Whole experiment | Mean of `shd` across all config pairs in an experiment. Lower means higher structural agreement across configs. |
| var_shd | Whole experiment | Variance of `shd` across config pairs. Higher means stability differs more across pairs. |
| n_configs | Whole experiment | Number of configs with usable restart data included in pairwise SHD comparisons. |
| n_pairs | Whole experiment | Number of compared config pairs, typically n_configs * (n_configs - 1) / 2. |
| bic_score | One summary row | Per-row BIC-like score read from summary.csv and aggregated later. |
| n_edges | One summary row | Number of edges in the learned DAG for that summary row/config. |
| bic_mean | MI setting aggregate | Mean `bic_score` over rows in one MI feature-selection setting (for example mi50 or mi100). |
| bic_std | MI setting aggregate | Standard deviation of `bic_score` over rows in one MI feature-selection setting. |
| edge_count_mean | MI setting aggregate | Mean `n_edges` over rows in one MI feature-selection setting. |
| edge_count_std | MI setting aggregate | Standard deviation of `n_edges` over rows in one MI feature-selection setting. |
| n_unique_dags | MI setting aggregate | MI-setting-level value derived from `n_distinct_optima`. |
| pairwise_shd_mean | MI setting aggregate | Mean pairwise SHD over best DAG per config for an MI feature-selection setting. |
| pairwise_shd_std | MI setting aggregate | Standard deviation of pairwise SHD over best DAG per config for an MI feature-selection setting. |
| n_distinct_optima | Input field | Number of distinct optimal DAGs reported in summary data. |

## Notes

- In this project, MI feature-selection setting labels like mi50 and mi100 indicate top-K mutual-information feature selection settings.
- Directed SHD is orientation-sensitive in this notebook.
