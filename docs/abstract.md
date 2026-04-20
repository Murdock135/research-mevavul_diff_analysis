# Abstract

Understanding which code changes co-occur with vulnerability fixes is a step toward automated patch assessment and vulnerability characterisation. Existing approaches largely treat patches as flat token or line diffs, without capturing the structured, operation-level semantics of AST edits. In this work, we fit a Bayesian network (BN) to a large-scale dataset of Java vulnerability patches drawn from MegaVul, learning a joint distribution over AST-level change type indicators and a binary vulnerability label. We extract diff features using the Coming tool, representing each function pair as a binary vector of 70 change type indicators (operation × node-type × parent-type triples). Structure learning via Hill-Climb Search with random restarts and BIC scoring yields a DAG with 69 edges (BIC = −24,095.95). In the learned structure, three change types appear as direct Markov blanket members of the vulnerability node, all insertion operations: insertion of an `if`-block, a block-level method call, and a block-level assignment. The fitted conditional probability distributions reveal a pronounced interaction among these three features: inserting an `if`-block in isolation is associated with a 43 percentage-point reduction in vulnerability probability, consistent with the pattern of guard-logic addition in security patches. However, this association reverses when an assignment insertion co-occurs, producing the highest vulnerability probability (0.802) observed in the model. Downstream of the vulnerability node, vulnerable functions are 3–4× more likely to co-occur with deletion of `if`-blocks and block-level invocations. These findings suggest that the joint configuration of change types — not individual operations in isolation — is the primary statistical discriminant between vulnerability-associated and patch-associated change profiles in this corpus, and that guard-logic insertions are the most prominent structural correlate of a security fix.

---

## Limitations and Reservations

- **Corpus scope.** The three-parent finding is specific to the MegaVul Java corpus. It would need replication on C/C++ data (e.g., PrimeVul) before any generalisation claim can be made across languages or ecosystems.

- **Associative, not causal.** Edge directions in the learned DAG reflect the structure that maximises BIC given the training data — they do not imply a causal or mechanistic relationship. The Markov blanket membership of the three insertion features should be read as a statement about statistical conditional independence in the fitted model, not about what causes vulnerabilities.

- **Interaction effect is a CPD property.** The reversal of the `if`-block association when `insert-node_Assignment_Block` is present is a property of the fitted conditional probability table, not a controlled experiment. It is consistent with the data but should not be over-interpreted without further investigation (e.g., manual inspection of the function pairs in that configuration).

- **BIC-selected structure is one of many near-equivalent DAGs.** The HCS restarts found a unique best DAG across 12 tied runs at this MI threshold, but nearby structures with similar BIC scores may assign different parents to `is_vul`. The three-parent result is stable under the chosen hyperparameters but sensitivity to MI threshold and tabu length has not been fully characterised.

- **Feature sparsity.** Most of the 70 change type indicators are present in fewer than 5% of function pairs. The BN is learned from a binary, sparse feature matrix; probability estimates in low-frequency cells of CPDs may be unreliable and are smoothed by the BDeu prior.

- **Label construction.** The `is_vul` label is derived from the direction of the diff (bug-fixing vs. bug-inducing direction), not from an independent vulnerability oracle. Mislabelled pairs or ambiguous commits could bias the learned structure.

---

## Threats to Validity

### Internal Validity

- **Structure learning optimisation.** Hill-Climb Search is a greedy local search; it can return locally optimal DAGs that are not globally optimal. Random restarts under HCS mitigate but do not eliminate this. The finding that 12 restarts converge to the same DAG at MI=50 provides some confidence, but a complete search is intractable.

- **Hyperparameter sensitivity.** The learned DAG — and therefore the identity of `is_vul`'s parents — depends on the MI threshold used for feature pre-selection, the tabu list length, and the maximum indegree constraint. These were selected by BIC-guided grid search but were not held out from the data used to fit the model. There is a risk of overfitting the structure to the specific dataset.

- **Non-independence of samples.** The dataset may contain multiple functions from the same CVE, repository, or developer. Functions from the same project likely share coding conventions that produce correlated change patterns. The BN assumes i.i.d. samples; violation of this assumption inflates effective sample size and may produce spuriously precise probability estimates.

- **Confounding from commit granularity.** A single commit can fix multiple vulnerabilities or mix security and non-security changes. The bug-inducing direction is a mechanical inversion of the patch direction and not an independently observed vulnerability introduction event. This construction introduces symmetric noise in the feature space.

### External Validity

- **Java-only corpus.** All results are derived exclusively from Java functions in MegaVul. Java's object-oriented idioms and type system influence what AST node types appear frequently; the three-parent result may not transfer to languages with different syntax or idioms (C, Python, Rust, etc.).

- **MegaVul sampling.** MegaVul covers publicly disclosed CVEs with available GitHub commits. This excludes privately patched vulnerabilities, vendor-patched proprietary code, and CVEs without a clean single-function patch. The distribution of change types in this sample may differ systematically from the broader population of vulnerability fixes.

- **Coming feature vocabulary.** The 70 features used are those produced by Coming's change-frequency extractor after MI-based filtering. Different feature extraction tools, granularities (e.g., statement-level vs. expression-level), or encodings (e.g., edit scripts, code embeddings) may reveal different structural relationships.

### Construct Validity

- **Binary vulnerability label.** Collapsing vulnerability state to a single binary variable loses information about severity (CVSS score), vulnerability class (CWE), and exploitability. The learned BN characterises the average change profile across all CVE types in the corpus, which may mask class-specific patterns.

- **Binary change features.** Change type indicators are binarised (present/absent per function pair). This discards count information — a function with ten `if`-block insertions is treated identically to one with a single insertion. Frequency-weighted features might yield a structurally different model.

- **Bayesian network expressiveness.** A BN over binary variables with BDeu priors is a relatively constrained model class. Non-linear interactions or threshold effects that cannot be expressed as conditional probability tables over discrete parents may be missed or distorted.
