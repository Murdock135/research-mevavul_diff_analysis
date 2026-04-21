# RQ3: Given a vulnerability-introducing commit, which other change types are more/less likely to co-occur?

## Method

To answer RQ3 we query the fitted BN for $P(X=1 \mid \text{is\_vul}=v)$ for each descendant node $X$ of `is_vul` under $v \in \{0,1\}$, estimated via likelihood-weighted sampling ($n=4{,}000$ draws). We partition descendants by the direction of their probability shift (higher or lower likelihood in vulnerability-introducing commits vs. security patches) and rank them by the absolute probability difference $|\Delta P| = |P(X=1 \mid \text{is\_vul}=1) - P(X=1 \mid \text{is\_vul}=0)|$ within each direction. We visualize all descendants in Figure~\ref{fig:descendants_scatter} (annotating the top three largest shifts in each direction), where the $x$-axis is the security patch baseline ($\text{is\_vul}=0$), the $y$-axis is the vulnerability-introducing condition ($\text{is\_vul}=1$), and the diagonal marks no difference.

## Results

Table 1 summarizes the descendant nodes most impacted by vulnerability status, ranked by their absolute probability difference ($|\Delta P|$).

**Table 1:** Top AST operations impacted by vulnerability status.
| AST Operation Node | $P(\text{is\_vul} = 0)$ | $P(\text{is\_vul} = 1)$ | $\Delta P$ |
| :--- | :---: | :---: | :---: |
| `delete-node_If_Block` | $0.053$ | $0.198$ | $+0.146$ |
| `delete-node_Invocation_Block` | $0.052$ | $0.176$ | $+0.124$ |
| `delete-node_LocalVariable_Block` | $0.071$ | $0.131$ | $+0.060$ |
| `insert-node_Invocation_Invocation` | $0.058$ | $0.026$ | $-0.033$ |
| `delete-node_Invocation_Invocation` | $0.019$ | $0.050$ | $+0.031$ |
| `delete-node_BinaryOperator_If` | $0.011$ | $0.036$ | $+0.025$ |
| `insert-node_Invocation_LocalVariable` | $0.026$ | $0.003$ | $-0.023$ |
| `insert-node_Parameter_Method` | $0.028$ | $0.007$ | $-0.021$ |

**Deletion changes characterize vulnerability-introducing commits** (red points above the diagonal in Figure~\ref{fig:descendants_scatter}). The three largest positive shifts belong to node-deletion operations: `delete-node_If_Block` ($\Delta P = +0.146$; rising from $P=0.053$ to $P=0.198$), `delete-node_Invocation_Block` ($\Delta P = +0.124$), and `delete-node_LocalVariable_Block` ($\Delta P = +0.060$). Smaller but consistent positive shifts are observed for `delete-node_Invocation_Invocation` ($+0.031$) and `delete-node_BinaryOperator_If` ($+0.025$). Collectively, these results indicate that vulnerability-introducing commits are characterized by the removal of conditional guards, method calls, and local variable declarations — precisely the structural elements that security patches tend to restore.

**Insertion changes are suppressed in vulnerability-introducing commits** (blue points below the diagonal in Figure~\ref{fig:descendants_scatter}). The three largest negative shifts are `insert-node_Invocation_Invocation` ($\Delta P = -0.033$; dropping from $P=0.058$ to $P=0.026$), `insert-node_Invocation_LocalVariable` ($\Delta P = -0.023$), and `insert-node_Parameter_Method` ($\Delta P = -0.021$). These change types correspond to adding nested method calls, local variable-bound invocations, and new method parameters — patterns consistent with defensive hardening such as input validation and resource scoping, which strongly co-occur with security patches.

## Interpretation

While RQ2 focuses on the direct causes of a vulnerability, RQ3 reveals the overall "shape" of a vulnerability-introducing commit. These commits have a distinct profile: they are heavily skewed toward deleting structures (like control flow and method calls) and systematically lack the defensive insertions that characterize security patches. In short, vulnerability-introducing commits are characterized by a broader pattern of structural removal rather than addition.

## Data Sources

- Figure (`fig:descendants_scatter`): [data/results/paper/figures/descendants_scatter_is_vul.png](../data/results/paper/figures/descendants_scatter_is_vul.png)
- Rankings table: `data/results/paper/tables/descendants_most_impacted_by_is_vul.csv`
- Full conditional probabilities: `data/results/paper/tables/descendant_conditional_probs_given_is_vul.csv`
- BN model: `data/results/bn1/bn1_hillclimb_mi50_tabu10_hcs/`
