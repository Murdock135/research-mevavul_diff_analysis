# MegaVul Diff Analysis

Research project analyzing vulnerability patches from the [MegaVul](https://github.com/Icyrockton/MegaVul) dataset. Extracts AST-level diff features from vulnerable/patched Java function pairs and learns a Bayesian network to understand what code changes fix specific vulnerability classes (CWEs).

> **PrimeVul (C/C++) analysis** lives in the sibling repo `../research-primevul_diff_analysis/`.

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

## Running the Pipeline

The full pipeline requires Docker (Coming tool is only available inside the container).

```bash
# Start the container
./scripts/_start.sh

# Inside Docker: run the full pipeline
uv run python pipeline_megavul_docker.py bug_fixing
uv run python pipeline_megavul_docker.py bug_inducing
```

Or step by step:

```bash
uv run python -m megavul_diff_analysis.scripts.megavul.extract
uv run python -m megavul_diff_analysis.scripts.megavul.create_pairs
uv run python -m megavul_diff_analysis.scripts.megavul.docker.get_diffs bug_fixing   # Docker
uv run python -m megavul_diff_analysis.scripts.megavul.docker.get_diffs bug_inducing # Docker
uv run python -m megavul_diff_analysis.scripts.megavul.build_feature_matrix
```

## BN Analysis (no Docker required)

| Script | Purpose | Config |
|---|---|---|
| `bn1/learn_dag_isvul.py` | Structure learning, single config | CLI args (`--help`) |
| `bn1/tune_bn1.py` | Grid search over hyperparameters | Edit globals at top of file |
| `bn1/fit_bn1.py` | Fit CPDs on learned structure | CLI args (`--help`) |
| `bn1/visualize_bn1.py` | Generate paper figures and tables | CLI args (`--help`) |

```bash
uv run python -m megavul_diff_analysis.scripts.megavul.bn1.learn_dag_isvul --help
uv run python -m megavul_diff_analysis.scripts.megavul.bn1.tune_bn1
```

## Documentation

See [CLAUDE.md](CLAUDE.md) for full architecture, data layout, and implementation notes.
