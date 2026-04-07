# Plan: BN1 Random Restarts via HCS Stopping Criterion

## Context

Two weaknesses in the current BN1 pipeline need addressing before the paper can make strong claims:

1. **Local maxima**: HillClimbSearch is greedy and always starts from an empty graph —
   a different starting point could reach a higher-scoring DAG. Random restarts address
   this, but a fixed restart count is arbitrary. The **HCS algorithm** (Dann, Dick & Wong,
   CMU 10-715 report) gives a principled, data-driven stopping criterion based on
   Good-Turing missing-mass estimation.

2. **Hyperparameter tuning**: Reviewers will ask how `tabu_length`, `max_indegree`, and
   `mi_threshold` were chosen. A grid search with BIC-d as the selection criterion provides
   a defensible answer.

**Key facts from exploration:**
- pgmpy 1.0.0 `HillClimbSearch.estimate()` accepts `start_dag=` ✓
- Scoring class is `pgmpy.estimators.BIC` with `.local_score(node, parents)` — sum over all nodes gives total BIC-d score
- pgmpy's own defaults: `tabu_length=100`, `max_iter=1_000_000` (our current values 10 / 1000 are undertuned)
- `BNPipeline.model_df` is preserved in the `.pkl`, enabling post-hoc scoring

---

## HCS Algorithm

Reference: Dann, Dick & Wong (CMU 10-715). Full report: https://www.cs.cmu.edu/~epxing/Class/10715/project-reports/DannDickWong.pdf

```
def hcs_structure_learning(data, delta, c):
    observed_models = []     # all local optima found so far
    n = 0

    while True:
        n += 1
        current_model = hill_climb_search(data, start=random_dag())
        observed_models.append(current_model)

        f1 = count_models_seen_exactly_once(observed_models)

        # Good-Turing upper bound on missing mass (prob. of unseen local optima)
        cn = f1 / n  +  (2*sqrt(2) + sqrt(3)) * sqrt(log(3 / delta) / n)

        if cn < c:
            return best_scoring_model(observed_models)
```

**Parameters:**
- `delta` (δ): confidence level — bound holds with probability ≥ 1−δ (e.g. 0.05)
- `c`: acceptable missing mass — stop when unseen optima hold < fraction c of total mass (e.g. 0.05)

**Intuition:**
- `f1/n` is the Good-Turing estimator of the probability the *next* restart finds a
  previously unseen local optimum. High f1 → still discovering new optima → keep going.
- The `(2√2+√3)·√(log(3/δ)/n)` term is a concentration correction that shrinks as n grows,
  providing the `1−δ` confidence guarantee.
- Two runs produce the "same" local optimum if their **edge sets are identical** (`frozenset(dag.edges())`).
- Best model is selected by highest BIC-d score across all observed local optima.

---

## Implementation Plan

### 1. `src/megavul_diff_analysis/bn/pipeline.py`

**New imports:**
```python
import random
import math
import networkx as nx
import numpy as np
from pgmpy.estimators import BIC as BicScore
```

**New module-level helper:**
```python
def _random_dag(nodes: list[str], n_edges: int, rng: random.Random) -> nx.DiGraph:
    """Random acyclic graph via topological-order trick:
    shuffle nodes to fix a random total order, then only add edges
    from lower-index to higher-index (guarantees acyclicity)."""
    dag = nx.DiGraph()
    dag.add_nodes_from(nodes)
    ordered = nodes[:]
    rng.shuffle(ordered)
    candidates = [
        (ordered[i], ordered[j])
        for i in range(len(ordered))
        for j in range(i + 1, len(ordered))
    ]
    rng.shuffle(candidates)
    for u, v in candidates[:n_edges]:
        dag.add_edge(u, v)
    return dag
```

**Modified `learn_structure()` signature:**
```python
def learn_structure(
    self,
    *,
    method: str = "hillclimb",
    scoring_method: str | None = None,
    significance_level: float = 0.01,
    ci_test: str = "chi_square",
    tabu_length: int = 100,           # updated default to match pgmpy
    max_indegree: int | None = None,
    max_iter: int = 1_000_000,        # updated default to match pgmpy
    hcs_delta: float = 0.05,          # confidence level (1 - delta)
    hcs_c: float = 0.05,              # acceptable missing mass
    hcs_max_restarts: int = 100,      # hard cap (safety valve)
    hcs_n_edges: int | None = None,   # random DAG density; default = n_nodes // 4
) -> "BNPipeline":
```

**HCS restart loop (hillclimb branch):**
```python
score    = scoring_method or "bic-d"
nodes    = list(self.model_df.columns)
n_edges  = hcs_n_edges if hcs_n_edges is not None else max(1, len(nodes) // 4)
scorer   = BicScore(self.model_df)
_CONST   = 2 * math.sqrt(2) + math.sqrt(3)

def dag_score(dag):
    return sum(scorer.local_score(n, list(dag.predecessors(n))) for n in dag.nodes())

edge_key_counts: dict[frozenset, int] = {}
best_dag, best_score = None, -np.inf

t0 = time.time()
for restart in range(hcs_max_restarts):
    n = restart + 1
    start_dag = (
        None if restart == 0
        else _random_dag(nodes, n_edges, random.Random(restart))
    )
    dag = HillClimbSearch(self.model_df).estimate(
        scoring_method=score, start_dag=start_dag,
        tabu_length=tabu_length, max_iter=max_iter,
        show_progress=False, **kwargs,
    )
    key = frozenset(dag.edges())
    s   = dag_score(dag)
    edge_key_counts[key] = edge_key_counts.get(key, 0) + 1

    if s > best_score:
        best_score, best_dag = s, dag

    f1 = sum(1 for cnt in edge_key_counts.values() if cnt == 1)
    cn = f1 / n + _CONST * math.sqrt(math.log(3 / hcs_delta) / n)
    logger.info(f"HCS restart {n}: score={s:.1f}, f1={f1}, cn={cn:.4f} (target<{hcs_c})")

    if cn < hcs_c:
        logger.info(f"HCS converged after {n} restarts ({time.time()-t0:.1f}s)")
        break
else:
    logger.warning(f"HCS hit hard cap of {hcs_max_restarts} restarts without converging")

self.edges = list(best_dag.edges())
```

---

### 2. `analysis/megavul/bn1/learn_dag_isvul.py`

Updated CLI args (replacing old `--tabu-length 10 --max-iter 1000`):

| Arg | Type | Default | Notes |
|---|---|---|---|
| `--tabu-length` | int | 100 | Updated from 10 to match pgmpy default |
| `--max-iter` | int | 1000000 | Updated from 1000 to match pgmpy default |
| `--hcs-delta` | float | 0.05 | Confidence parameter δ |
| `--hcs-c` | float | 0.05 | Missing-mass target c |
| `--hcs-max-restarts` | int | 100 | Hard cap on restarts |
| `--hcs-n-edges` | int | None | Edges in random starting DAG (default n_nodes//4) |

Updated `build_stem()`:
```python
def build_stem(method, mi_threshold):
    mi_tag = f"_mi{mi_threshold}" if mi_threshold is not None else ""
    return f"bn1_{method}{mi_tag}_hcs"
```

---

### 3. `analysis/megavul/bn1/tune_bn1.py`

Grid search over preprocessing and structure learning hyperparameters.
HCS handles restart count adaptively — only tune the "outer" hyperparameters.

**Grid (27 configs):**
```python
GRID = {
    "mi_threshold":  [50, 100, 200],
    "tabu_length":   [10, 50, 100],
    "max_indegree":  [None, 3, 5],
}
HCS_DELTA = 0.05
HCS_C     = 0.10   # looser during grid search for speed
HCS_MAX   = 50
```

**Per-config output columns:** `mi_threshold`, `tabu_length`, `max_indegree`,
`n_restarts_used`, `n_edges`, `bic_score`, `n_edges_on_target`, `elapsed_s`

**Output:** `data/results/tune_bn1_grid.csv` sorted by `bic_score` descending.

---

## Verification

```bash
# 1. Smoke-test: should converge in a few restarts with loose c
uv run python analysis/megavul/bn1/learn_dag_isvul.py \
  --mi-threshold 50 --hcs-delta 0.05 --hcs-c 0.20 --hcs-max-restarts 10

# 2. Full grid search (~10–30 min)
uv run python analysis/megavul/bn1/tune_bn1.py

# 3. Inspect results
head -5 data/results/tune_bn1/<timestamp>/summary.csv

# 4. Production run with best config + tight HCS
uv run python analysis/megavul/bn1/learn_dag_isvul.py \
  --mi-threshold <best> --tabu-length <best> --max-indegree <best> \
  --hcs-delta 0.05 --hcs-c 0.05 --hcs-max-restarts 100

# 5. Fit and visualize as before
uv run python analysis/megavul/bn1/fit_bn1.py \
  --pipeline-file data/results/bn1/<stem>_pipeline.pkl
uv run python analysis/megavul/bn1/visualize_bn1.py \
  --pipeline-file data/results/bn1/<stem>_pipeline.pkl \
  --model-file data/results/bn1/<stem>_fitted.pkl
```
