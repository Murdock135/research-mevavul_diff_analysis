# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Research project analyzing vulnerability patches from the [MegaVul](https://github.com/Icyrockton/MegaVul) dataset. The goal is to extract AST-level diff features from vulnerable/patched Java function pairs and use them in a Bayesian network to understand what kinds of code changes fix specific vulnerability classes (CWEs).

> **PrimeVul (C/C++) analysis has been moved** to the sibling repo `../research-primevul_diff_analysis/`.

- **MegaVul**: Java functions from `data/raw/megavul/cve_with_graph_abstract_commit.json`

## Environment

The full pipeline **must be run inside Docker** — Coming (Java AST diff tool) is only available there.

```bash
# Start the Docker container (prompts to rebuild)
./scripts/_start.sh

# Inside Docker — run full pipeline
uv run python pipeline_megavul_docker.py bug_fixing
uv run python pipeline_megavul_docker.py bug_inducing

# Or step by step:
uv run python -m megavul_diff_analysis.scripts.megavul.extract
uv run python -m megavul_diff_analysis.scripts.megavul.create_pairs
uv run python -m megavul_diff_analysis.scripts.megavul.docker.get_diffs bug_fixing
uv run python -m megavul_diff_analysis.scripts.megavul.docker.get_diffs bug_inducing
uv run python -m megavul_diff_analysis.scripts.megavul.build_feature_matrix

# BN1 analysis (no Docker required):
uv run python -m megavul_diff_analysis.scripts.megavul.bn1.learn_dag_isvul
uv run python -m megavul_diff_analysis.scripts.megavul.bn1.fit_bn1 --pipeline-file <path>
uv run python -m megavul_diff_analysis.scripts.megavul.bn1.visualize_bn1
```

Docker internals: container name `megavul-analysis`, Coming JAR at `/opt/coming.jar`, GumTree at `/opt/gumtree`, `JAVA_OPTS=-Xmx4G`. The `.` directory is mounted at `/app`; `.venv` is a separate volume so local edits are immediately reflected inside.

## Project Structure

```
src/megavul_diff_analysis/        # Main Python package
├── datapreparator/
│   └── megavul.py                # MegaVulExtractor + MegaVulDataPreparator
├── difftools/
│   └── coming_tool.py            # ComingTool (subprocess wrapper for Coming JAR)
├── scripts/
│   └── megavul/
│       ├── extract.py            # Step 1: JSON → megavul_pairs.csv
│       ├── create_pairs.py       # Step 2: CSV → pair directories
│       ├── docker/               # ← requires Docker
│       │   └── get_diffs.py      # Step 3: run Coming on pairs
│       ├── build_feature_matrix.py  # Step 4: Coming outputs → feature_matrix.parquet
│       ├── experiment_tracker.py    # Grid search result tracking
│       ├── bn_utils.py              # BNPipeline + HCS implementation
│       └── bn1/                  # BN1: code changes → is_vul
│           ├── learn_dag_isvul.py
│           ├── fit_bn1.py
│           ├── tune_bn1.py
│           ├── visualize_bn1.py
│           └── run_missing_paper_configs.py
├── types.py                      # Pydantic data models (MegaVul + Coming types)
└── utils/
    ├── config_utils.py           # find_project_root()
    ├── logging.py
    └── str_utils.py

pipeline_megavul_docker.py        # Full pipeline (steps 1-3) — run inside Docker
scripts/                          # Shell scripts (_start.sh, _delete.sh, etc.)
```

## Data Layout

```
data/
├── raw/
│   └── megavul/                           # LFS-tracked
│       └── cve_with_graph_abstract_commit.json
├── interim/                               # gitignored
│   └── megavul/                           # pair dirs written by create_pairs.py
│       └── <commit_hash>/<func_name>/
│           ├── <hash>_<func>_s.java
│           ├── <hash>_<func>_t.java
│           ├── metadata.json
│           ├── change_frequency_bug_fixing.json    # written by get_diffs.py
│           └── change_frequency_bug_inducing.json
├── processed/                             # gitignored
│   └── megavul/
│       ├── megavul_pairs.csv              # written by extract.py
│       └── feature_matrix.parquet        # written by build_feature_matrix.py
└── results/                              # gitignored
    ├── bn1/                              # learn_dag_isvul.py + fit_bn1.py outputs
    │   ├── <stem>_edges.csv/json
    │   ├── <stem>_pipeline.pkl
    │   └── <stem>_fitted.pkl
    ├── figures/bn1/
    ├── tables/bn1/
    ├── paper/                            # run_missing_paper_configs.py outputs
    └── tune_bn1/                         # tune_bn1.py outputs (timestamped dirs)
```

## Analysis Pipeline

### MegaVul (Java, Coming)

1. **`extract.py`** — Reads MegaVul JSON (CVE → commits → files → functions), saves `data/processed/megavul/megavul_pairs.csv`.
2. **`create_pairs.py`** — Writes pair directories to `data/interim/megavul/<commit_hash>/<func_name>/`. Java snippets are wrapped in a class declaration so Spoon can parse them.
3. **`docker/get_diffs.py`** *(Docker required)* — Runs Coming on each pair directory, saves `change_frequency_{direction}.json` (`direction` = `bug_fixing` or `bug_inducing`).
4. **`build_feature_matrix.py`** — Reads all `change_frequency_*.json` files, builds binary feature matrix, saves `data/processed/megavul/feature_matrix.parquet`.

### BN1: code changes → is_vul

5. **`bn1/learn_dag_isvul.py`** — Loads `feature_matrix.parquet`, runs HillClimbSearch with HCS random restarts, saves edges + pipeline snapshot to `data/results/bn1/`.
6. **`bn1/fit_bn1.py`** — Loads pipeline snapshot, fits CPDs with BDeu prior, saves fitted model.
7. **`bn1/tune_bn1.py`** — Grid search over MI threshold × tabu length × max indegree. Outputs to `data/results/tune_bn1/<timestamp>/`.
8. **`bn1/visualize_bn1.py`** — Generates paper figures (DAG, heatmaps, MI bar chart) and tables. Outputs to `data/results/figures/bn1/` and `data/results/tables/bn1/`.

## Key Architecture

### Core Types ([src/megavul_diff_analysis/types.py](src/megavul_diff_analysis/types.py))
All data structures are Pydantic `BaseModel`s:
- `MegaVCodePair` — One vulnerable/patched Java function pair.
- `ComingChangeFrequency` / `ComingRunResult` — Coming tool output types.
- `FuncDir` / `HashDir` / `ComingPairDataDir` — Directory structure models.

### Coming Tool ([src/megavul_diff_analysis/difftools/coming_tool.py](src/megavul_diff_analysis/difftools/coming_tool.py))
- `ComingTool` — Subprocess wrapper for the Coming JAR. `analyze_pair` accepts configurable `source_file`/`target_file` names and an `output_suffix` for direction (`bug_fixing` / `bug_inducing`).

### Data Preparation ([src/megavul_diff_analysis/datapreparator/megavul.py](src/megavul_diff_analysis/datapreparator/megavul.py))
- `MegaVulExtractor` — Loads nested MegaVul JSON, creates `MegaVCodePair` per function.
- `MegaVulDataPreparator.write_single_pair()` — Directories named `<commit_hash>/<func_name>` (with numeric suffix for same-name collisions within a commit). Java snippets wrapped in `class <func_name> { ... }` so Spoon produces non-empty ASTs.

### BN Infrastructure ([src/megavul_diff_analysis/scripts/megavul/bn_utils.py](src/megavul_diff_analysis/scripts/megavul/bn_utils.py))
- `BNPipeline` — Stateful pipeline: `preprocess()` → `learn_structure()` → `fit()`. Each method returns `self` for chaining. Intermediate state picklable for reproducibility.
- HCS random restarts: random DAG initialization, restart loop with configurable δ/c/max_restarts, edge inclusion frequency tracking across restarts.

### Project Root Detection
`find_project_root()` in [`src/megavul_diff_analysis/utils/config_utils.py`](src/megavul_diff_analysis/utils/config_utils.py) walks up from `__file__` to find the directory containing `pyproject.toml`. All scripts use this for absolute paths.

## Important Notes

- **MegaVul Java wrapping**: bare method snippets are wrapped in `class <func_name> { ... }` so Spoon (Coming's parser) produces non-empty ASTs.
- **MegaVul collision disambiguation**: pairs sharing the same `(commit_hash, func_name)` get a numeric suffix (`func_name_1`, `func_name_2`, etc.).
- **Feature matrix label**: `is_vul=False` comes from `bug_fixing` direction (vulnerable→patched), `is_vul=True` from `bug_inducing` (patched→vulnerable).
- **BN1 output stem**: controlled by `--method` and `--mi-threshold` args to `learn_dag_isvul.py`, e.g. `bn1_hillclimb_mi100_hcs`.
- **`fit_bn1.py` input**: takes `--pipeline-file` pointing to a `<stem>_pipeline.pkl` from `learn_dag_isvul.py`. The stem is inferred automatically.
