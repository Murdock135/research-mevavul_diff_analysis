# Grid Search Observations — tune_bn1

**Date:** 2026-03-03
**Run:** `data/results/tune_bn1/20260303_225055/`
**Status at time of writing:** 4/27 configs completed (all mi=50)

---

## Results so far

| # | mi | tabu | indeg | restarts | distinct | edges | bic | on_target | elapsed_s |
|---|---|---|---|---|---|---|---|---|---|
| 1 | 50 | 10 | None | 50 | 45 | 69 | -24095.95 | 31 | 342.8 |
| 2 | 50 | 10 | 3    | 50 | 45 | 69 | -24095.95 | 31 | 330.6 |
| 3 | 50 | 10 | 5    | 50 | 45 | 69 | -24095.95 | 31 | 344.1 |
| 4 | 50 | 50 | None | 50 | 45 | 69 | -24095.95 | 31 | 341.2 |

---

## Observations

### 1. HCS never converged — hit the hard cap on every config
`n_restarts_used = 50` on all 4 configs (the hard cap). With 45/50 restarts finding
distinct local optima, `cn` never drops below `hcs_c = 0.10`. The search space is
extremely diverse — almost every restart reaches a new DAG. This means:
- 50 restarts is too few to characterise the optimum landscape for mi=50
- The problem may require significantly more restarts or a tighter `hcs_c` to converge

### 2. `max_indegree` has no effect
Configs 1/2/3 (indeg=None/3/5) produce *identical* BIC score, edge count, and target
edge count. The `max_indegree` constraint never binds because the unconstrained HC
solution naturally has ≤ 3 parents per node (average degree = 69/51 ≈ 1.35). This
will likely persist for mi=100 and mi=200 unless larger feature sets force denser
structures.

**Implication for paper:** `max_indegree` can probably be dropped from the grid or
fixed at None without loss. The relevant hyperparameters are `mi_threshold` and
`tabu_length`.

### 3. `tabu_length` has no effect so far
Configs 1 and 4 (tabu=10 vs tabu=50) are identical. The same local optima are being
found regardless of tabu list length for mi=50. This may change for larger mi values
where the landscape is more complex.

### 4. 31/69 edges on `is_vul` is suspicious
Nearly 45% of all edges touch the target node. BIC-d tends to over-connect hub nodes
because adding parents to a node always improves its local score in isolation.
Worth examining: how many of those 31 are parents vs. children of `is_vul`? A high
parent count suggests the BIC structure is absorbing most features as direct predictors
rather than finding conditional independence structure.

---

## Open questions

- Will `mi=100` and `mi=200` produce meaningfully different BIC scores?
- Does any config achieve HCS convergence (cn < 0.10) within 50 restarts?
- Are the 45 distinct optima genuinely different structures, or minor edge-flip variants?
- What is the parent/child split of the 31 target edges?
