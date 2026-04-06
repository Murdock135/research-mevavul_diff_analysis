# MegaVul Diff Analysis

Research project analyzing vulnerability patches from the [MegaVul](https://github.com/Icyrockton/MegaVul) dataset. Extracts AST-level diff features from vulnerable/patched Java function pairs and learns a Bayesian network to understand what code changes are associated with fixing specific vulnerability classes (CWEs).

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

---

## Pipeline

The pipeline has two phases. **Phase 1 requires Docker** (Coming tool); Phase 2 does not.

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

## Documentation

See [CLAUDE.md](CLAUDE.md) for architecture details, data layout, and implementation notes.
