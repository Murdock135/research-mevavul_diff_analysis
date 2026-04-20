I'm in read-only Ask mode and cannot write files. Here's the content ready to paste into `docs/RQ2.md`:

---

# RQ2: Which code change types are most significant for predicting vulnerability outcomes?

## Direct Predictors of `is_vul`

The learned DAG (MI=50, tabu=10, BIC = −24,095.95) assigns exactly **three direct parents** to `is_vul`, all insertion operations:

| Node | Operation | AST context |
|---|---|---|
| `insert-node_If_Block` | insert | `if`-statement inserted into a block |
| `insert-node_Invocation_Block` | insert | method call inserted into a block |
| `insert-node_Assignment_Block` | insert | assignment inserted into a block |

Their joint CPD shows large probability swings across the four parent state combinations:

| `Assignment_Block` | `If_Block` | `Invocation_Block` | P(is_vul=1) |
|---|---|---|---|
| 0 | 0 | 0 | **0.592** (most vulnerable) |
| 0 | 0 | 1 | 0.235 |
| 0 | 1 | 0 | **0.158** (least vulnerable) |
| 0 | 1 | 1 | 0.168 |

Inserting an `if`-block alone reduces P(is_vul) from 0.592 → 0.158 — a **43 percentage-point drop** and the single strongest direct predictor in the model. Inserting an invocation-in-block reduces it to 0.235. The most vulnerable configuration is the absence of all three changes.

## Model-Implied Correlations with `is_vul`

From the CPD-implied correlation matrix (10,000 ancestral samples), the top correlates by |r| are:

| Node | r | Direction |
|---|---|---|
| `insert-node_If_Block` | −0.251 | lower when vulnerable |
| `insert-node_Invocation_Block` | −0.176 | lower when vulnerable |
| `insert-node_LocalVariable_Block` | −0.102 | lower when vulnerable |
| `insert-node_Invocation_LocalVariable` | −0.090 | lower when vulnerable |
| `delete-node_Invocation_LocalVariable` | +0.088 | higher when vulnerable |
| `delete-node_Parameter_Method` | +0.083 | higher when vulnerable |
| `insert-node_Parameter_Method` | −0.082 | lower when vulnerable |

The pattern is consistent: **insertions correlate negatively with vulnerability; deletions correlate positively**.

## Co-occurrence Patterns in Descendants of `is_vul`

Top descendant nodes ranked by |ΔP(X=1)| = |P(X=1 | is_vul=1) − P(X=1 | is_vul=0)| (likelihood weighting, 4,000 draws):

| Node | P(·\|vul=1) | P(·\|vul=0) | Δ | Direction |
|---|---|---|---|---|
| `delete-node_If_Block` | 0.198 | 0.053 | +0.146 | higher when vulnerable |
| `delete-node_Invocation_Block` | 0.176 | 0.052 | +0.124 | higher when vulnerable |
| `delete-node_LocalVariable_Block` | 0.131 | 0.071 | +0.060 | higher when vulnerable |
| `insert-node_Invocation_Invocation` | 0.026 | 0.058 | −0.033 | lower when vulnerable |
| `delete-node_Invocation_Invocation` | 0.050 | 0.019 | +0.031 | higher when vulnerable |
| `delete-node_BinaryOperator_If` | 0.036 | 0.011 | +0.025 | higher when vulnerable |
| `insert-node_Invocation_LocalVariable` | 0.003 | 0.026 | −0.023 | lower when vulnerable |
| `insert-node_Parameter_Method` | 0.007 | 0.028 | −0.021 | lower when vulnerable |

## Summary

The most significant change types are **insertions of control-flow and invocation structures**. Adding an `if`-block (guard/validation logic) is the single strongest predictor of a non-vulnerable outcome — a 43 pp reduction in P(is_vul) in the CPD, and r = −0.25 in the model-implied correlation. The symmetric signal appears in the descendant layer: vulnerable functions are 3–4× more likely to co-occur with *deletion* of `if`-blocks and invocations (`delete-node_If_Block` Δ = +0.146, `delete-node_Invocation_Block` Δ = +0.124), reinforcing the interpretation that patches predominantly **add guard logic and method calls**, while vulnerable originals lack them.

### Caveat

Edge directions in the DAG reflect statistical fit under BIC, not necessarily causal mechanism. The parent–child relationships around `is_vul` should be interpreted as associations: changes that co-occur with the vulnerable/patched label, not necessarily as causes or effects in a mechanistic sense.


*Data sources: `data/results/paper/tables/vuln_parent_state_probs.csv`, `cpd_implied_correlation_matrix.csv`, `descendants_most_impacted_by_is_vul.csv`. Best DAG: `bn1_hillclimb_mi50_tabu10_hcs`, BIC = −24,095.95, 69 edges.*

---

## Camera-Ready Text

**RQ2: Which code change types are most significant for predicting vulnerability outcomes?**

The best-fit DAG identifies three change types as the direct probabilistic predictors of vulnerability status: insertion of an \texttt{if}-block (\texttt{insert-node\_If\_Block}), insertion of a method call within a block (\texttt{insert-node\_Invocation\_Block}), and insertion of an assignment within a block (\texttt{insert-node\_Assignment\_Block}). All three are insertion operations. Figure~\ref{fig:vuln_bar_chart} shows the full conditional probability distribution P(\texttt{is\_vul} | parents) across all eight parent state combinations.

The CPD (Table~\ref{tab:vuln_parent_state_probs}) reveals a pronounced interaction effect among these three predictors. When the assignment insertion is absent, adding an \texttt{if}-block is strongly protective: P(\texttt{is\_vul}=1) falls from 0.592 (no insertions) to 0.158 — a 43 percentage-point reduction. Adding an invocation insertion in isolation reduces it to 0.235. However, this protective effect reverses when the assignment insertion is also present. With all three insertions active, P(\texttt{is\_vul}=1) reaches its model maximum of 0.802. With the assignment and \texttt{if}-block present but no invocation, vulnerability probability remains elevated at 0.541. This interaction indicates that \texttt{insert-node\_Assignment\_Block} is not independently protective; its co-occurrence with the other two insertions characterises a qualitatively different — and highly vulnerable — change profile rather than a patching pattern.

The descendant layer of the DAG reinforces the directional signal. Conditioning on \texttt{is\_vul} via likelihood-weighted inference (4,000 draws, Table~\ref{tab:descendants_most_impacted}) shows that vulnerable functions are substantially more likely to co-occur with deletion of \texttt{if}-blocks ($\Delta P = +0.146$) and deletion of block-level invocations ($\Delta P = +0.124$), while patched functions more frequently co-occur with insertion of nested invocations and parameters. Taken together, the BN structure and parameters indicate that the most discriminative changes concern the addition or removal of control-flow guards and method calls at the block level — but their predictive significance depends on their joint configuration rather than their individual presence.

We note that edge directions in the DAG reflect the optimal structure under BIC given the training data, and do not imply a causal mechanism. The associations above should be interpreted as statistical co-occurrence patterns between change types and vulnerability labels.

<!-- Table sources for LaTeX conversion:
  \label{tab:vuln_parent_state_probs}
    → data/results/paper/tables/vuln_parent_state_probs.csv
    Columns: insert-node_Assignment_Block, insert-node_If_Block, insert-node_Invocation_Block, P(Not Vulnerable), P(Vulnerable)

  \label{tab:descendants_most_impacted}
    → data/results/paper/tables/descendants_most_impacted_by_is_vul.csv
    Columns: node, P(state=1 | is_vul=0), P(state=1 | is_vul=1), abs_delta, impact_direction

  \label{fig:vuln_bar_chart}
    → data/results/paper/figures/vuln_bar_chart.png
-->