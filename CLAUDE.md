# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Research project analyzing vulnerability patches from the [PrimeVul dataset](https://github.com/DLVulDet/PrimeVul). The goal is to extract AST-level diff features from vulnerable/patched C function pairs and use them (e.g., in a Bayesian network) to understand what kinds of code changes fix specific vulnerability classes (CWEs).

The primary dataset file is `PrimeVul_v0.1/primevul_train_paired.jsonl`.

## Environment

The project runs inside Docker (recommended), which provides GumTree and the Coming tool (Java-based AST diff tools) alongside the Python environment. **GumTree is only available inside Docker** at `/usr/local/bin/gumtree`; steps 1–3 of the pipeline require it.

```bash
# Start the Docker container (prompts to rebuild)
./_start.sh

# Inside Docker, run the main pipeline (steps 1-3)
uv run primevul-analysis        # or: uv run python -m primevul_analysis

# Run individual pipeline scripts (steps 4-6, inside or outside Docker)
uv run python src/primevul_analysis/scripts/extract_features.py
uv run python src/primevul_analysis/scripts/merge_features_with_labels.py
uv run python src/primevul_analysis/scripts/bayesian_network_analysis.py
```

Outside Docker (Python only, no GumTree/Coming):

```bash
uv sync          # Install dependencies
```

Docker internals: container name is `primevul-analysis`, Coming JAR at `/opt/coming.jar`, `GUMTREE_HOME=/opt/gumtree`, `JAVA_OPTS=-Xmx4G`. The `.` directory is mounted at `/app`; `.venv` is a separate volume so local edits are immediately reflected inside.

## Analysis Pipeline

Scripts under `src/primevul_analysis/scripts/`. Each reads from `data/` and writes back to `data/`:

1. **`extract_data.py`** — Reads `PrimeVul_v0.1/primevul_train_paired.jsonl`, filters to commits with exactly one vulnerable + one patched function, saves `data/code_pairs.csv`.
2. **`create_pairs.py`** — Writes each pair as `data/coming_data/pair_NNNNN/{vulnerable.c, patched.c, metadata.json}`.
3. **`get_diffs.py`** — Runs GumTree on each pair directory (**requires Docker**), saves XML diff files inside `data/coming_data/pair_NNNNN/`.
4. **`extract_features.py`** — Parses all GumTree XML diffs, extracts numeric features, saves `data/gumtree_features.csv`.
5. **`merge_features_with_labels.py`** — Joins features with `code_pairs.csv` on `extraction_index` (sequential position in file, not the `id` field), saves `data/features_with_labels.csv`.
6. **`bayesian_network_analysis.py`** — Trains a Bayesian network (HillClimbSearch + BIC) on `data/features_with_labels.csv`; saves graph and edges to `data/bayesian_network/`. Set `cwe_of_interest` and `USE_MANUAL`/`TOP_K` constants at the top of `main()` before running.

`__main__.py` chains steps 1–3. Steps 4–6 must be run individually.

## Key Architecture

### Core Types ([src/primevul_analysis/types.py](src/primevul_analysis/types.py))
All data structures are Pydantic `BaseModel`s:
- `CodePair` — One vulnerable/patched function pair from PrimeVul.
- `GumTreeDiffResult` — Raw subprocess output from GumTree (note: a comment in the file flags this for renaming to `GumTreeToolOutput`).
- `GumTreeDiff` — Parsed AST diff: `actions` + `matches`, plus span-keyed lookup dicts `src_to_dst` / `dst_to_src`.
- `GumTreeAction` / `GumTreeMatch` / `NodeRef` — Fine-grained diff primitives.
- `ChangeGroup` — A `@dataclass` (not Pydantic) grouping overlapping-span actions into a hunk.

### Diff Tools ([src/primevul_analysis/difftools/](src/primevul_analysis/difftools/))
- `GumTreeTool` — Subprocess wrapper for `gumtree textdiff`. Raises `FileNotFoundError` at init if GumTree is not on PATH.
- `GumTreeToolPairsExecutor` — Batch-processes `pair_NNNNN/` directories using `ThreadPoolExecutor`. **Sorts pair dirs before processing** to ensure deterministic `extraction_index` ordering.
- `ComingTool` — Subprocess wrapper for the Coming JAR.

### Parsers ([src/primevul_analysis/parsers/gumtree_parser.py](src/primevul_analysis/parsers/gumtree_parser.py))
- `GumTreeDiffParser` — Parses GumTree XML output. GumTree emits two sibling top-level elements (`<matches>` + `<actions>`); the parser wraps them in `<root>` to satisfy `ElementTree`. Node ref strings like `"if_statement: foo [10, 42]"` are parsed with regex into `NodeRef`. GumTree uses the attribute name `dest` (not `dst`) for match destinations.

### Feature Extractors ([src/primevul_analysis/feature_extractors/gumtree.py](src/primevul_analysis/feature_extractors/gumtree.py))
- `GumTreeFeatureExtractor.extract()` — Returns a flat `dict` of scalars. Feature naming conventions:
  - `n_*` — counts (actions, matches, groups, etc.)
  - `act_*` — per-kind action counts (raw GumTree kind strings, e.g. `act_delete-node`)
  - `node_*` — top-K counts by canonical node type (e.g. `node_if_statement`)
  - `touches_*` / `deleted_*` / `inserted_*` — boolean flags for control-flow/security motifs
  - `ratio_*` — normalized ratios
  - `has_*` — boolean heuristics (e.g. `has_safety_token_update`)
- Hunk grouping (`group_actions`) clusters actions by overlapping source spans, controlled by `span_merge_gap`.
- Security features (`_security_features`) require both `vulnerable_code` and `patched_code` text; without them, boundary-check features default to `False`.

### Data Preparation ([src/primevul_analysis/datapreparator/extract.py](src/primevul_analysis/datapreparator/extract.py))
- `PrimeVulExtractor` — Loads the JSONL, groups by `commit_id`, keeps only commits with exactly one `target==1` (vulnerable) and one `target==0` (patched) row.
- `DataPreparator.write_single_pair()` — Directories named `pair_{index:05d}`, so the 5-digit zero-padded index equals `extraction_index`.

### Project Root Detection
`find_project_root()` in [`src/primevul_analysis/utils/config_utils.py`](src/primevul_analysis/utils/config_utils.py) walks up from `__file__` to find the directory containing `pyproject.toml`. All scripts use this for absolute paths.

## Notebooks

Exploratory notebooks in `notebooks/`. Generated reports/figures go to `notebooks/results/`.

## Important Notes

- **Pair ordering is the join key**: `merge_features_with_labels.py` joins on `extraction_index` (row position in `code_pairs.csv`), not the `id` field. The directory `pair_NNNNN` number matches this index. `batch_analyze_pair_dirs` sorts dirs alphabetically before processing to keep this consistent.
- **GumTree non-zero exit codes are normal**: GumTree returns non-zero when files differ (expected). Errors are only flagged when `stderr` is non-empty.
- **Feature CSV column prefixes** used by `bayesian_network_analysis.py` to auto-discover candidate columns: `n_`, `node_`, `act_`, `touches_`, `deleted_`, `inserted_`, `ratio_`, `has_`.
- **BN target CWE**: change `cwe_of_interest` constant in `bayesian_network_analysis.py:main()` before running. The `primary_cwe` column is the first element of each pair's CWE list.
