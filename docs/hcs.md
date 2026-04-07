# HCS Stopping Criterion for Hill-Climb Search

Reference: Dann, Dick & Wong (CMU 10-715). Full report: https://www.cs.cmu.edu/~epxing/Class/10715/project-reports/DannDickWong.pdf

## Algorithm

```
def hcs_structure_learning(data, delta, c):
    observed_models = []

    for n in 1, 2, ...:
        current_model = hill_climb_search(data, start=random_dag())
        observed_models.append(current_model)

        f1 = count_models_seen_exactly_once(observed_models)

        # Good-Turing upper bound on missing mass
        cn = f1 / n  +  (2√2 + √3) · √(log(3/δ) / n)

        if cn < c:
            return best_scoring_model(observed_models)
```

## Parameters

| Parameter | Symbol | Default | Meaning |
|-----------|--------|---------|---------|
| `hcs_delta` | δ | 0.05 | Confidence level — bound holds with probability ≥ 1−δ |
| `hcs_c` | c | 0.05 | Acceptable missing mass — stop when unseen optima hold < fraction c of total probability mass |
| `hcs_max_restarts` | — | 100 | Hard cap on restarts (safety valve if convergence is slow) |
| `hcs_n_edges` | — | n_nodes // 4 | Number of edges in each random starting DAG |

## Intuition

- `f1/n` is the Good-Turing estimator of the probability the *next* restart finds a previously unseen local optimum. High f1 means the search is still discovering new optima — keep going.
- The `(2√2+√3)·√(log(3/δ)/n)` term is a concentration correction that shrinks as n grows, providing the 1−δ confidence guarantee.
- Two runs are considered the same local optimum if their **edge sets are identical** (`frozenset(dag.edges())`).
- The best model is selected by highest BIC-d score across all restarts.
