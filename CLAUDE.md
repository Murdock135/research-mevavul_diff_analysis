# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Research project analyzing vulnerability patches from the [PrimeVul](https://github.com/DLVulDet/PrimeVul) and [MegaVul](https://github.com/Icyrockton/MegaVul) datasets. The goal is to extract AST-level diff features from vulnerable/patched function pairs and use them (e.g., in a Bayesian network) to understand what kinds of code changes fix specific vulnerability classes (CWEs).

- **PrimeVul**: C/C++ functions from `data/raw/PrimeVul_v0.1/primevul_train_paired.jsonl`
- **MegaVul**: Java functions from `data/raw/megavul/cve_with_graph_abstract_commit.json`

## Environment

The project runs inside Docker (recommended), which provides GumTree and the Coming tool (Java-based AST diff tools) alongside the Python environment. **GumTree and Coming are only available inside Docker**.

```bash
# Start the Docker container (prompts to rebuild)
./_start.sh

# Inside Docker — run full pipeline for each dataset
uv run python pipeline_primevul.py   # PrimeVul: steps 1-3
uv run python pipeline_megavul.py    # MegaVul:  steps 1-3

# Or step by step (primevul example):
uv run python -m diff_analysis.scripts.primevul.extract_data
uv run python -m diff_analysis.scripts.primevul.create_pairs
uv run python -m diff_analysis.scripts.primevul.get_diffs
uv run python -m diff_analysis.scripts.primevul.extract_features
uv run python -m diff_analysis.scripts.primevul.merge_features_with_labels
uv run python -m diff_analysis.scripts.primevul.bayesian_network_analysis
```

Outside Docker (Python only, no GumTree/Coming):

```bash
uv sync          # Install dependencies
```

Docker internals: container name is `primevul-analysis`, Coming JAR at `/opt/coming.jar`, `GUMTREE_HOME=/opt/gumtree`, `JAVA_OPTS=-Xmx4G`. The `.` directory is mounted at `/app`; `.venv` is a separate volume so local edits are immediately reflected inside.

## Project Structure

```
src/diff_analysis/           # Main Python package (importable as diff_analysis)
├── datapreparator/
│   ├── primevul.py          # PrimeVulExtractor + DataPreparator
│   └── megavul.py           # MegaVulExtractor + MegaVulDataPreparator
├── difftools/
│   ├── gumtree.py           # GumTreeTool + GumTreeToolPairsExecutor
│   └── coming_tool.py       # ComingTool
├── feature_extractors/
│   └── gumtree.py           # GumTreeFeatureExtractor
├── parsers/
│   └── gumtree_parser.py    # GumTreeDiffParser
├── scripts/
│   ├── primevul/            # Individual pipeline steps for PrimeVul
│   │   ├── extract_data.py
│   │   ├── create_pairs.py
│   │   ├── get_diffs.py
│   │   ├── extract_features.py
│   │   ├── merge_features_with_labels.py
│   │   └── bayesian_network_analysis.py
│   └── megavul/             # Individual pipeline steps for MegaVul
│       ├── extract.py
│       ├── create_pairs.py
│       └── get_diffs.py
├── types.py                 # Pydantic data models
└── utils/

pipeline_primevul.py         # Full PrimeVul pipeline (steps 1-3) — run at project root
pipeline_megavul.py          # Full MegaVul pipeline (steps 1-3) — run at project root
scripts/                     # Utility/Docker scripts (fix-permissions.sh, etc.)
```

## Data Layout

```
data/
├── raw/                     # Downloaded, unmodified datasets (git-tracked via LFS where applicable)
│   ├── PrimeVul_v0.1/       # gitignored locally (large); copy manually
│   └── megavul/             # LFS-tracked
├── interim/                 # Intermediate pipeline artifacts (gitignored)
│   ├── coming_data/         # PrimeVul pair dirs + GumTree XML diffs
│   └── megavul_pairs/       # MegaVul pair dirs + Coming change_frequency_{direction}.json
├── processed/               # Analysis-ready CSVs (gitignored)
│   ├── code_pairs.csv
│   ├── megavul_pairs.csv
│   ├── gumtree_features.csv
│   └── features_with_labels.csv
└── results/                 # Final outputs (gitignored)
    └── bayesian_network/
```

## Analysis Pipelines

### PrimeVul (C/C++, GumTree)

1. **`extract_data.py`** — Reads JSONL, filters to commits with exactly one vulnerable + one patched function, saves `data/processed/code_pairs.csv`.
2. **`create_pairs.py`** — Writes each pair as `data/interim/coming_data/pair_NNNNN/{vulnerable.c, patched.c, metadata.json}`.
3. **`get_diffs.py`** — Runs GumTree on each pair directory (**requires Docker**), saves XML diff files inside `data/interim/coming_data/pair_NNNNN/`.
4. **`extract_features.py`** — Parses all GumTree XML diffs, extracts numeric features, saves `data/processed/gumtree_features.csv`.
5. **`merge_features_with_labels.py`** — Joins features with `code_pairs.csv` on `extraction_index`, saves `data/processed/features_with_labels.csv`.
6. **`bayesian_network_analysis.py`** — Trains a Bayesian network (HillClimbSearch + BIC); saves graph and edges to `data/results/bayesian_network/`. Set `cwe_of_interest` and `USE_MANUAL`/`TOP_K` constants at the top of `main()` before running.

### MegaVul (Java, Coming)

1. **`extract.py`** — Reads MegaVul JSON, saves `data/processed/megavul_pairs.csv`.
2. **`create_pairs.py`** — Writes pair directories to `data/interim/megavul_pairs/<commit_hash>/<func_name>/`. Java snippets are wrapped in a class declaration so Spoon can parse them.
3. **`get_diffs.py`** — Runs Coming on each pair directory (**requires Docker**), saves `change_frequency_{direction}.json` inside each pair dir (`direction` = `bug_fixing` or `bug_inducing`).

## Key Architecture

### Core Types ([src/diff_analysis/types.py](src/diff_analysis/types.py))
All data structures are Pydantic `BaseModel`s:
- `PrimeVCodePair` — One vulnerable/patched C function pair from PrimeVul.
- `MegaVCodePair` — One vulnerable/patched Java function pair from MegaVul.
- `GumTreeDiff` — Parsed AST diff: `actions` + `matches`, plus span-keyed lookup dicts.
- `GumTreeAction` / `GumTreeMatch` / `NodeRef` — Fine-grained diff primitives.
- `ChangeGroup` — A `@dataclass` grouping overlapping-span actions into a hunk.

### Diff Tools ([src/diff_analysis/difftools/](src/diff_analysis/difftools/))
- `GumTreeTool` — Subprocess wrapper for `gumtree textdiff`. Raises `FileNotFoundError` at init if GumTree is not on PATH.
- `GumTreeToolPairsExecutor` — Batch-processes `pair_NNNNN/` directories using `ThreadPoolExecutor`. **Sorts pair dirs before processing** to ensure deterministic `extraction_index` ordering.
- `ComingTool` — Subprocess wrapper for the Coming JAR. `analyze_pair` accepts configurable `source_file`/`target_file` names.

### Parsers ([src/diff_analysis/parsers/gumtree_parser.py](src/diff_analysis/parsers/gumtree_parser.py))
- `GumTreeDiffParser` — Parses GumTree XML output. GumTree emits two sibling top-level elements (`<matches>` + `<actions>`); the parser wraps them in `<root>` to satisfy `ElementTree`. Node ref strings like `"if_statement: foo [10, 42]"` are parsed with regex into `NodeRef`. GumTree uses the attribute name `dest` (not `dst`) for match destinations.

### Feature Extractors ([src/diff_analysis/feature_extractors/gumtree.py](src/diff_analysis/feature_extractors/gumtree.py))
- `GumTreeFeatureExtractor.extract()` — Returns a flat `dict` of scalars. Feature naming conventions:
  - `n_*` — counts (actions, matches, groups, etc.)
  - `act_*` — per-kind action counts (raw GumTree kind strings, e.g. `act_delete-node`)
  - `node_*` — top-K counts by canonical node type (e.g. `node_if_statement`)
  - `touches_*` / `deleted_*` / `inserted_*` — boolean flags for control-flow/security motifs
  - `ratio_*` — normalized ratios
  - `has_*` — boolean heuristics (e.g. `has_safety_token_update`)
- Hunk grouping (`group_actions`) clusters actions by overlapping source spans, controlled by `span_merge_gap`.
- Security features (`_security_features`) require both `vulnerable_code` and `patched_code` text.

### Data Preparation
- `PrimeVulExtractor` — Loads JSONL, groups by `commit_id`, keeps only commits with exactly one `target==1` (vulnerable) and one `target==0` (patched) row.
- `DataPreparator.write_single_pair()` — Directories named `pair_{index:05d}`; the 5-digit zero-padded index equals `extraction_index`.
- `MegaVulExtractor` — Loads nested MegaVul JSON (CVE → commits → files → vulnerable_functions).
- `MegaVulDataPreparator.write_single_pair()` — Directories named `<commit_hash>/<func_name>` (with numeric suffix for same-name collisions). Java snippets wrapped in `class <func_name> { ... }`.

### Project Root Detection
`find_project_root()` in [`src/diff_analysis/utils/config_utils.py`](src/diff_analysis/utils/config_utils.py) walks up from `__file__` to find the directory containing `pyproject.toml`. All scripts use this for absolute paths.

## Notebooks

Exploratory notebooks in `notebooks/`. Generated reports/figures go to `notebooks/results/`.

## Important Notes

- **Pair ordering is the join key**: `merge_features_with_labels.py` joins on `extraction_index` (row position in `code_pairs.csv`), not the `id` field. The directory `pair_NNNNN` number matches this index. `batch_analyze_pair_dirs` sorts dirs alphabetically before processing to keep this consistent.
- **GumTree non-zero exit codes are normal**: GumTree returns non-zero when files differ (expected). Errors are only flagged when `stderr` is non-empty.
- **Feature CSV column prefixes** used by `bayesian_network_analysis.py` to auto-discover candidate columns: `n_`, `node_`, `act_`, `touches_`, `deleted_`, `inserted_`, `ratio_`, `has_`.
- **BN target CWE**: change `cwe_of_interest` constant in `bayesian_network_analysis.py:main()` before running. The `primary_cwe` column is the first element of each pair's CWE list.
- **MegaVul Java wrapping**: bare method snippets are wrapped in `class <func_name> { ... }` so Spoon (Coming's parser) can produce non-empty ASTs.
- **MegaVul collision disambiguation**: pairs sharing the same `(commit_hash, func_name)` get a numeric suffix on the directory (`func_name_1`, `func_name_2`, etc.).
