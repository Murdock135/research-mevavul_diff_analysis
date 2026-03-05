# Possible Analyses

Data sources: `data/results/paper/` (tuning runs), `data/results/` (single best BN per MI level),
`data/results/figures/`, `data/results/tables/`.

---

## From `summary.csv` — Hyperparameter Sensitivity

- **BIC stability across configs**: at MI=50, BIC is nearly identical across all 7 configs
  (−24095 to −24096) — quantifies robustness of the learned structure to tabu/indegree choices.
- **Edge count stability**: all MI=50 configs produce 69–70 edges — strong claim that the BN
  structure is hyperparameter-insensitive.
- **n_distinct_optima**: 45/50 restarts found distinct optima at MI=50 — tells you about search
  landscape ruggedness.
- **MI=50 vs MI=100 comparison**: 51 nodes / 69 edges vs 90 nodes / 118 edges — how much does
  relaxing the feature threshold change the structure?
- **Runtime analysis**: elapsed_s across configs (parallelism efficiency, per-config cost).

---

## From `hcs_restarts.jsonl` — HCS Search Landscape (richest source)

- **Score convergence curve**: BIC score vs. restart number — does HCS plateau early or keep
  improving?
- **Edge stability across restarts**: which edges appear in *all* 50 restarts (core structure)
  vs. only a few (fragile/noisy) — a principled way to identify the most trustworthy edges.
- **Search landscape variance**: distribution of BIC scores across restarts, per config.
- **Core edge comparison across configs**: do the stable edges agree across all 7 configs?
  Cross-validates the hyperparameter stability finding.

---

## From `edges.csv` + `structure.csv` — BN Structure

- **Direction of `is_vul` connections**: 3 features are parents of `is_vul` (direct predictors),
  27–30 are children (features conditioned on vulnerability state) — this asymmetry is
  interpretable and worth discussing.
- **Symmetric insert/delete pairs**: `insert-node_X` and `delete-node_X` for the same node type
  appear as neighbours — the BN captures complementary patch operations.
- **MI=50 vs MI=100 edge overlap**: Jaccard similarity of edge sets — which edges are preserved,
  which are new at MI=100.

---

## From `lift.csv` — Probabilistic Interpretation

- **Risk-elevating vs. risk-reducing features**: `delete-node_If_Block` lift=1.9,
  `insert-node_If_Block` lift=0.27 — deleting a conditional block is a strong vulnerability
  signal, inserting one is protective.
- **Directionality of If/Invocation changes**: consistent pattern across both MI levels.

---

## From `mi_top20.csv` — Feature Selection

- **Action type distribution**: are insert vs. delete equally informative, or is one dominant?
- **AST node type ranking**: If_Block and Invocation_Block dominate — the semantically meaningful
  change sites.
- **MI score dropoff**: how quickly does MI fall after the top features? Justifies threshold
  choice.

---

## Cross-cutting (most paper-worthy)

1. **Structural robustness claim**: combine summary stability + restart edge stability to argue
   the BN is not a search artifact.
2. **Core edge identification**: edges present in ≥X% of restarts across all configs → the
   reliable skeleton of the model.
3. **Interpretable patch vocabulary**: the 3 features that are direct parents of `is_vul` are
   the most actionable finding — what they are and what they mean structurally.
