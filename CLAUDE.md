# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Research project analyzing vulnerability patches from the [PrimeVul dataset](https://github.com/DLVulDet/PrimeVul). The goal is to extract AST-level diff features from vulnerable/patched C function pairs and use them (e.g., in a Bayesian network) to understand what kinds of code changes fix specific vulnerability classes (CWEs).

The primary dataset file is `PrimeVul_v0.1/primevul_train_paired.jsonl`.

## Environment

The project runs inside Docker (recommended), which provides GumTree and the Coming tool (Java-based AST diff tools) alongside the Python environment. All analysis tools require GumTree to be installed at `/usr/local/bin/gumtree`.

```bash
# Start the Docker container (prompts to rebuild)
./_start.sh

# Inside Docker, the Python package is installed via uv
uv run primevul-analysis        # Run the main CLI entrypoint
uv run python -m primevul_analysis  # Equivalent
```

Outside Docker (Python only, no GumTree/Coming):

```bash
uv sync          # Install dependencies
uv run python src/primevul_analysis/scripts/<script>.py
```

## Analysis Pipeline

The pipeline runs as a sequence of standalone scripts under `src/primevul_analysis/scripts/`. Each script reads from `data/` and writes back to `data/`:

1. **`extract_data.py`** — Reads `PrimeVul_v0.1/primevul_train_paired.jsonl`, extracts vulnerable/patched function pairs, saves `data/code_pairs.csv`.
2. **`create_pairs.py`** — Writes each pair as `data/coming_data/pair_NNNNN/{vulnerable.c, patched.c, metadata.json}`.
3. **`get_diffs.py`** — Runs GumTree on each pair directory, saves XML diff files alongside the `.c` files in `data/coming_data/`.
4. **`extract_features.py`** — Parses all GumTree XML diffs, extracts numeric features, saves `data/gumtree_features.csv`.
5. **`merge_features_with_labels.py`** — Joins features with `code_pairs.csv` on extraction order (not ID), saves `data/features_with_labels.csv`.
6. **`bayesian_network_analysis.py`** — Loads `data/features_with_labels.csv`, trains a Bayesian network (HillClimbSearch + BIC), saves graph and edges to `data/bayesian_network/`.

The `__main__.py` entrypoint chains steps 1–3 together. Steps 4–6 must be run individually.

## Key Architecture

### Core Types (`src/primevul_analysis/types.py`)
All data structures are Pydantic `BaseModel`s:
- `CodePair` — One vulnerable/patched function pair from PrimeVul.
- `GumTreeDiffResult` — Raw subprocess output from GumTree.
- `GumTreeDiff` — Parsed AST diff: `actions` (insert/delete/update/move) + `matches` (node correspondences), with span-indexed lookup maps.
- `GumTreeAction` / `GumTreeMatch` / `NodeRef` — Fine-grained diff primitives.
- `ComingRunResult` / `ComingChangeFrequency` — Results from the Coming tool.

### Diff Tools (`src/primevul_analysis/difftools/`)
- `GumTreeTool` — Subprocess wrapper for `gumtree textdiff`. Default output format: XML.
- `GumTreeToolPairsExecutor` — Batch-processes `pair_NNNNN/` directories using `ThreadPoolExecutor`.
- `ComingTool` — Subprocess wrapper for the Coming JAR.

### Parsers (`src/primevul_analysis/parsers/`)
- `GumTreeDiffParser` — Parses GumTree XML output (handles GumTree's non-standard two-root XML) into `GumTreeDiff`. Node ref strings like `"if_statement: foo [10, 42]"` are parsed with regex into `NodeRef`.

### Feature Extractors (`src/primevul_analysis/feature_extractors/`)
- `GumTreeFeatureExtractor` — Converts a `GumTreeDiff` into a flat dict of numeric features (action counts by type/node, touch flags for control flow, safety tokens, etc.).

### Data Preparation (`src/primevul_analysis/datapreparator/`)
- `PrimeVulExtractor` — Loads the JSONL, groups by `commit_id`, keeps only commits with exactly one vulnerable + one patched function.
- `DataPreparator` — Writes `pair_NNNNN/` directories to disk.

### Project Root Detection
`find_project_root()` in `utils/config_utils.py` walks up from `__file__` to find the directory containing `pyproject.toml`. All scripts use this to build absolute paths.

## Notebooks

Exploratory notebooks live in `notebooks/`. Generated reports/figures go to `notebooks/results/`. There is an `notebooks/AGENT_GUIDE.md` (missing at time of writing) intended for agent-assisted analysis.

## Important Notes

- **Pair ordering matters**: `merge_features_with_labels.py` joins on `extraction_index` (sequential order of pairs in `code_pairs.csv`), not on the `id` field. The pair directory number `pair_NNNNN` matches this index.
- **GumTree non-zero exit codes are normal**: GumTree returns non-zero when files differ, which is the expected case. Errors are only flagged when `stderr` is non-empty.
- **Docker mounts `.` to `/app`** with a separate volume for `.venv`, so local edits are immediately reflected inside the container.
- **`GUMTREE_HOME=/opt/gumtree`** and `JAVA_OPTS=-Xmx4G` are set in the container environment.
