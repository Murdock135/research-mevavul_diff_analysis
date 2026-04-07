# Documentation

The documents here are meant to be read alongside the code, not instead of it. They record the reasoning behind decisions, observations from experiments, and plans for what comes next. What follows is a guide to reading them in an order that builds understanding naturally. An [index](#index) is provided at the bottom for quick reference.

---

## 1. Where the data comes from

The project begins with a dataset. MegaVul is a collection of real-world CVEs, each linked to a commit in which a developer fixed a vulnerability. From that commit, we extract two versions of each affected function: the vulnerable one and its patch. Before anything else, it helps to understand the shape of this data — how CVEs, commits, and functions are nested inside the JSON, and which fields we actually use. The [data model](megavul-data-model.md) document is that orientation.

## 2. Turning code changes into numbers

Once we have the function pairs, the next question is: what do we measure? We want to know what *changed* between the vulnerable and patched versions — not at the text level, but at the level of the abstract syntax tree. For this we use Coming, a Java AST diff tool. Coming compares two `.java` files and counts how many times each type of AST change appears: nodes inserted, deleted, updated, moved, and so on. The output is a vector of change counts per pair, and all pairs together form the feature matrix. The [feature matrix notes](feature-matrix-notes.md) document what this matrix looks like in practice — its shape, how sparse it is, and what the early data exploration revealed.

## 3. Learning structure without getting stuck

With the feature matrix in hand, we want to learn a Bayesian network that relates these change types to whether a function is vulnerable. The natural starting point is hill-climb search, but greedy search over DAG space is prone to local maxima, and the result depends on where you start. It is also sensitive to hyperparameter choices that are hard to justify post-hoc. We address this with a random restart strategy based on the [HCS stopping criterion](hcs.md) (Dann, Dick & Wong), which uses Good-Turing missing-mass estimation to decide when enough restarts have been run, combined with a grid search over hyperparameters. The [hyperparameters](hyperparameters.md) document is the reference companion — a table of every tunable parameter, what it controls, and what values we settled on.

## 4. What the grid search taught us

After running the grid search, we had results across 27 configurations. The [grid search observations](discussions/grid-search-observations.md) record what we found: which configurations were stable, where the search landscape was rough, and what this tells us about how much to trust the learned structure. This is where the empirical justification for the final configuration lives.

## 5. What we still want to know

From there, [planned analyses](planned-analyses.md) describes the analyses we intend to run on the fitted model — organized by what data source they draw from and what claim they support in the paper. Some are done; most are still open.

## 6. Reference material

The [metrics reference](metrics-reference.md) defines every metric that appears in the results notebooks, and [todo.md](todo.md) tracks what still needs to be built — figures, tables, and future model variants (BN2 over CWE, BN3 over severity).

---

## Index

| Document | What it covers |
|----------|----------------|
| [megavul-data-model.md](megavul-data-model.md) | Shape of the raw MegaVul JSON: CVE entries, commit structure, function fields |
| [feature-matrix-notes.md](feature-matrix-notes.md) | What the feature matrix looks like after Coming runs: shape, sparsity, column naming |
| [hcs.md](hcs.md) | HCS stopping criterion: algorithm, parameters, and intuition |
| [hyperparameters.md](hyperparameters.md) | Every tunable parameter for structure learning and fitting, with chosen values |
| [discussions/grid-search-observations.md](discussions/grid-search-observations.md) | Empirical findings from the 27-config grid search; justification for the final configuration |
| [planned-analyses.md](planned-analyses.md) | Analyses planned for the paper, organized by data source; early interpretable findings |
| [metrics-reference.md](metrics-reference.md) | Definitions of every metric reported in the results notebooks |
| [todo.md](todo.md) | Open tasks: figures, tables, BN2/BN3 variants, infrastructure |
