# MegaVul Diff Analysis

> **PrimeVul (C/C++) analysis** lives in the sibling repo `../research-primevul_diff_analysis/`.

> **New to this project?** Start with [docs/](docs/README.md) for a guided introduction to the data, the methods, and the reasoning behind the design.

## What this project does

This project investigates what kinds of code changes fix software vulnerabilities. The starting point is [MegaVul](https://github.com/Icyrockton/MegaVul), a dataset of real-world CVEs where each entry contains a vulnerable Java function and its patched counterpart from the same commit. The central question is: do certain structural code changes — adding a null check, removing a method call, changing a condition — reliably appear in fixes for specific vulnerability classes?

## How it works

To answer this, we first extract the vulnerable/patched function pairs from MegaVul's JSON and write each pair as two `.java` files on disk. We then run [Coming](https://github.com/SpoonLabs/coming), a Java AST diff tool, on every pair to count how many times each type of AST change appears in the diff. Coming requires a JVM and specific dependencies, so this step runs inside a Docker container. We run Coming twice per pair: once in the natural direction (vulnerable → patched, labelled `is_vul=False`) and once in reverse (patched → vulnerable, labelled `is_vul=True`), which gives us a balanced binary classification dataset where the label reflects whether a given change profile corresponds to a vulnerability being introduced or fixed.

The change counts from Coming are aggregated into a binary feature matrix — each row is one function pair in one direction, each column is one AST change type. This matrix is the input to Phase 2, which requires no Docker. We learn a Bayesian network DAG over the features using hill-climb search with random restarts, fit conditional probability distributions on the learned structure, and generate figures and tables for the paper.

> For a more comprehensive explanation of the data, design decisions, and analysis — including the reasoning behind the HCS restart strategy and what the grid search revealed — see [docs/](docs/README.md).

## How to read this repo

The [Pipeline](#pipeline) section below shows the full flow with the diagram and step-by-step tables. The [Data Layout](#data-layout) section is a reference for where every file lives and which script produces it. For implementation details and architecture notes, see [CLAUDE.md](CLAUDE.md).

## Prerequisites

- [Docker](https://docs.docker.com/get-docker/)
- [uv](https://docs.astral.sh/uv/)
- git LFS (`git lfs install`)

## Setup

```bash
git clone <repo>
cd research-lm_error_analysis
uv sync
```

---

## Pipeline

The pipeline has two phases. **Phase 1 requires Docker** (Coming tool); Phase 2 does not.

```
 MegaVul JSON
      │
      ▼
┌─────────────────────────────────────────────────────┐
│  PHASE 1 — Docker required                          │
│                                                     │
│  extract.py  ──►  megavul_pairs.csv                 │
│                          │                          │
│                          ▼                          │
│             create_pairs.py                         │
│                          │                          │
│            interim/megavul/<commit>/<func>/         │
│                    ┌─────┴─────┐                    │
│                    ▼           ▼                    │
│          get_diffs.py    get_diffs.py               │
│          bug_fixing      bug_inducing               │
│         (vul → patch)   (patch → vul)               │
│          is_vul=False    is_vul=True                │
│                    └─────┬─────┘                    │
│                          ▼                          │
│            build_feature_matrix.py                  │
│                          │                          │
│                 feature_matrix.parquet              │
└─────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────┐
│  PHASE 2 — No Docker                                │
│                                                     │
│  learn_dag_isvul.py ──► <stem>_pipeline.pkl         │
│  (or tune_bn1.py for grid search)                   │
│                          │                          │
│                          ▼                          │
│                      fit_bn1.py                     │
│                          │                          │
│                  <stem>_fitted.pkl                  │
│                          │                          │
│                          ▼                          │
│                  visualize_bn1.py                   │
│                          │                          │
│              figures/, tables/, paper/              │
└─────────────────────────────────────────────────────┘
```

### Phase 1 — Data preparation (Docker required)

Run via `./scripts/_start.sh` then `uv run python pipeline_megavul_docker.py bug_fixing` inside the container, or step by step:

| Step | Script | Input | Output |
|------|--------|-------|--------|
| 1 | `extract.py` | `data/raw/megavul/*.json` | `data/processed/megavul/megavul_pairs.csv` |
| 2 | `create_pairs.py` | `megavul_pairs.csv` | `data/interim/megavul/<commit>/<func>/` |
| 3 ⚠️ | `docker/get_diffs.py bug_fixing` | pair directories | `change_frequency_bug_fixing.json` per pair |
| 3 ⚠️ | `docker/get_diffs.py bug_inducing` | pair directories | `change_frequency_bug_inducing.json` per pair |
| 4 | `build_feature_matrix.py` | pair directories + change JSONs | `data/processed/megavul/feature_matrix.parquet` |

> ⚠️ Step 3 runs the [Coming](https://github.com/SpoonLabs/coming) AST diff tool, which requires the Docker container.
>
> Step 3 is run twice — `bug_fixing` treats the vulnerable function as source (producing `is_vul=False` rows) and `bug_inducing` swaps the direction (producing `is_vul=True` rows). Together they form a balanced dataset.

### Phase 2 — Bayesian network analysis (no Docker)

| Step | Script | Purpose | Config |
|------|--------|---------|--------|
| 5 | `bn1/learn_dag_isvul.py` | Learn DAG structure via HillClimbSearch + random restarts | CLI args (`--help`) |
| 5 (alt) | `bn1/tune_bn1.py` | Grid search over MI threshold × tabu length × max indegree | Edit globals at top of file |
| 6 | `bn1/fit_bn1.py` | Fit conditional probability distributions on learned structure | CLI args (`--help`) |
| 7 | `bn1/visualize_bn1.py` | Generate paper figures (DAG, heatmaps, MI bar chart) and tables | CLI args (`--help`) |

```bash
# Structure learning
uv run python -m megavul_diff_analysis.scripts.megavul.bn1.learn_dag_isvul --mi-threshold 100 --tabu-length 50

# Or grid search
uv run python -m megavul_diff_analysis.scripts.megavul.bn1.tune_bn1

# Fit and visualize
uv run python -m megavul_diff_analysis.scripts.megavul.bn1.fit_bn1 --pipeline-file data/results/bn1/<stem>_pipeline.pkl
uv run python -m megavul_diff_analysis.scripts.megavul.bn1.visualize_bn1 --help
```

---

## Data Layout

```
data/
├── raw/
│   └── megavul/                                      # git LFS tracked
│       └── cve_with_graph_abstract_commit.json       # MegaVul source
│
├── interim/                                          # gitignored — regenerate via pipeline
│   └── megavul/
│       └── <commit_hash>/<func_name>/
│           ├── <hash>_<func>_s.java                  # vulnerable function
│           ├── <hash>_<func>_t.java                  # patched function
│           ├── metadata.json
│           ├── change_frequency_bug_fixing.json      # Coming output (vul→patch)
│           └── change_frequency_bug_inducing.json    # Coming output (patch→vul)
│
├── processed/                                        # gitignored — regenerate via pipeline
│   └── megavul/
│       ├── megavul_pairs.csv                         # extract.py output
│       └── feature_matrix.parquet                   # build_feature_matrix.py output
│
└── results/                                          # gitignored (except paper/)
    ├── bn1/                                          # learn_dag_isvul.py + fit_bn1.py
    │   ├── <stem>_edges.csv
    │   ├── <stem>_pipeline.pkl
    │   └── <stem>_fitted.pkl
    ├── figures/bn1/                                  # visualize_bn1.py
    ├── tables/bn1/                                   # visualize_bn1.py
    ├── paper/                                        # git tracked — final outputs
    └── tune_bn1/                                     # tune_bn1.py (timestamped dirs)
```

---

## Documentation

See [CLAUDE.md](CLAUDE.md) for architecture details and implementation notes.
