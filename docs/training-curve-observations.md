# HCS Structure Learning: Convergence Analysis

Sources: `notebooks/training_curve.ipynb`, `notebooks/structure_learning_results.ipynb`  
Data: 9 hyperparameter configurations × 2 MI thresholds = 18 runs, 50 restarts each; 32 valid configs and 1,621 restart records total (including tuning runs).  
Figures: `data/results/figures/bn1/training_curve_{bic,cn}.pdf`

**Key result:** across all hyperparameter settings and random seeds, a single unique best DAG was recovered (BIC-D = −24,095.95, 69 edges, found at restart 11). All 12 restarts that reached the top score — spanning every combination of tabu length and indegree constraint at MI=50 — produced identical edge sets (`unique_best_dags = 1`). The analysis below documents how the training curve evidence supports and qualifies this finding.

---

## Search Adequacy

To assess whether the random-restart hill-climb search (HCS) produced reliable structure estimates, we tracked two convergence signals across restarts: the running-best BIC-D score and the Good-Turing bound $c_n$.

The running-best BIC-D score — the highest score observed up to restart $n$ — is monotonically non-decreasing and plateaus once the search has found its best DAG. At MI=50, this plateau is reached within approximately 10–15 restarts for most configurations, suggesting the 50-restart budget is more than sufficient at this threshold. At MI=100, however, the running-best continues to improve well into restarts 40–50 for the highest-scoring configurations (`tabu=100, indeg=∞` and `tabu=100, indeg=5`), indicating that the search had not saturated within the allotted budget. This difference is attributable to the sparser candidate graph produced by the higher MI filter: fewer candidate edges yield a flatter score landscape in which distinct high-scoring DAGs are more numerous and harder to distinguish.

The Good-Turing bound $c_n$ provides a complementary measure of search coverage. It estimates the fraction of probability mass held by DAG topologies not yet seen, and HCS is designed to stop when $c_n$ falls below a threshold `hcs_c`. Across all 18 runs, $c_n$ decays sharply from approximately 10.3 at restart 1 and stabilises around 2.3 by restart 50 — more than 20 times above the stopping threshold of 0.1. Consequently, no run was terminated by the HCS convergence criterion; all were cut off by the hard restart cap of 50. Notably, the $c_n$ curves are nearly indistinguishable across all nine hyperparameter settings at MI=100, and nearly so at MI=50, indicating that tabu length and maximum indegree have no material effect on the rate of mode-space exploration.

Taken together, these signals initially suggest a tension — score convergence does not imply structural convergence. However, a cross-run analysis of the globally optimal score resolves this: across all 1,621 restart records, 12 restarts tied at the top BIC-D score of −24,095.95, and all 12 produced **identical edge sets** (`unique_best_dags = 1`). The winning DAG was first found at restart 11. This indicates that the score landscape has a single dominant structural attractor at MI=50: despite the search formally not satisfying the HCS stopping criterion, all hyperparameter settings and random seeds converge to the same DAG whenever they reach the global optimum.

## Hyperparameter Sensitivity

The top 10 configurations by BIC-D score are exclusively MI=50 runs, with identical scores (−24,095.95), edge counts (69), and edges incident on the target node `is_vul` (31), spanning all combinations of tabu length ∈ {10, 50} and max indegree ∈ {∞, 3, 5}. No MI=100 configuration appears in the top 10. This confirms that the MI threshold is the dominant hyperparameter: it determines both the achievable score ceiling and the structural outcome, while tabu length and indegree constraint are effectively irrelevant at MI=50. With the exception of `tabu=50, indeg=5`, which produces intermittent severe score collapses attributable to the joint constraint stranding the hill-climber in dead-ends, all configurations yield identical best scores. The $c_n$ curves being uniform across settings provides the mechanistic explanation: no configuration explores the mode space faster than any other, so final quality is determined entirely by which modes exist, not by how they are searched.

## Limitations

The edge inclusion frequencies used to select the final DAG structure are computed across 50 restarts. Since $c_n \gg$ `hcs_c` at termination, these frequencies are estimated from an incomplete sample of the mode space. The `unique_best_dags = 1` result substantially mitigates this concern for the globally optimal score, but the frequencies of sub-optimal edges — those not present in every top-scoring DAG — remain uncertain. The `hcs_c` stopping threshold of 0.1 was fixed a priori and was not subjected to sensitivity analysis; a more permissive threshold would declare convergence far earlier, while a stricter one would require substantially more computation. For MI=100 in particular, neither the score nor the structural evidence supports the claim that 50 restarts is sufficient: the running-best is still improving near restart 50, and MI=100 does not appear among the top-scoring configurations at all.
