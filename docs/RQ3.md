# RQ3: Given a function is vulnerable, which other change types are more/less likely to co-occur?

## Method

To answer RQ3 we query the fitted BN for $P(X=1 \mid \text{is\_vul}=v)$ for each descendant node $X$ of `is_vul` under $v \in \{0,1\}$, estimated via likelihood-weighted sampling ($n=4{,}000$ draws). We rank descendants by the absolute probability shift $|\Delta P| = |P(X=1 \mid \text{is\_vul}=1) - P(X=1 \mid \text{is\_vul}=0)|$ and visualize all descendants in Figure~\ref{fig:descendants_scatter}, where the $x$-axis is the non-vulnerable baseline, the $y$-axis is the vulnerable condition, and the diagonal marks no difference.

## Results

**Deletion changes co-occur with vulnerability** (red points above the diagonal in Figure~\ref{fig:descendants_scatter}). The three largest positive shifts belong to node-deletion operations: `delete-node_If_Block` ($\Delta P = +0.146$; rising from $P=0.053$ to $P=0.198$), `delete-node_Invocation_Block` ($\Delta P = +0.124$), and `delete-node_LocalVariable_Block` ($\Delta P = +0.060$). Smaller but consistent positive shifts are observed for `delete-node_Invocation_Invocation` ($+0.031$) and `delete-node_BinaryOperator_If` ($+0.025$). Collectively, these results indicate that vulnerable functions disproportionately co-occur with the removal of conditional guards, method calls, and local variable declarations — precisely the structural elements that security patches tend to restore.

**Insertion changes are suppressed under vulnerability** (blue points below the diagonal in Figure~\ref{fig:descendants_scatter}). The three largest negative shifts are `insert-node_Invocation_Invocation` ($\Delta P = -0.033$; dropping from $P=0.058$ to $P=0.026$), `insert-node_Invocation_LocalVariable` ($\Delta P = -0.023$), and `insert-node_Parameter_Method` ($\Delta P = -0.021$). These change types correspond to adding nested method calls, local variable-bound invocations, and new method parameters — patterns consistent with defensive hardening such as input validation and resource scoping.

## Interpretation

The results reveal a structural co-occurrence signature of vulnerability: vulnerable functions systematically lack the insertion changes that characterise security patches, while exhibiting elevated rates of node deletions targeting control flow and invocation structures. This extends the direct-parent findings of RQ2 to the full downstream neighbourhood of `is_vul` in the learned DAG.

## Data Sources

- Figure (`fig:descendants_scatter`): [data/results/paper/figures/descendants_scatter_is_vul_split.png](../data/results/paper/figures/descendants_scatter_is_vul_split.png)
- Rankings table: `data/results/paper/tables/descendants_most_impacted_by_is_vul.csv`
- Full conditional probabilities: `data/results/paper/tables/descendant_conditional_probs_given_is_vul.csv`
- BN model: `data/results/bn1/bn1_hillclimb_mi50_tabu10_hcs/`
