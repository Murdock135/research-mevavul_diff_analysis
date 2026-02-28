# Refactoring Plan

## Package & Imports
- [ ] Rename package directory `src/primevul_analysis/` → `src/diff_analysis/` and update `pyproject.toml` project name to `diff-analysis`
- [ ] Update all imports from `primevul_analysis` → `diff_analysis` across every Python file

## Module Cleanup
- [ ] Rename `datapreparator/extract.py` → `datapreparator/primevul.py`
- [ ] Remove dead modules: `config/settings.py` and `logging/logger.py`

## Scripts
- [ ] Group scripts by dataset: `scripts/primevul/` and `scripts/megavul/`
- [ ] Move docker/utility scripts into project root `scripts/` (fix-permissions.sh, etc.)

## Pipeline Entry Points
- [ ] Create `pipeline_primevul.py` at project root (move logic from `__main__.py`)
- [ ] Create `pipeline_megavul.py` at project root
- [ ] Make `__main__.py` a minimal dispatcher (`python -m diff_analysis primevul/megavul`)

## Data Directory
- [ ] Restructure `data/`: create `raw/`, `interim/`, `processed/`, `results/` subdirectories
- [ ] Move `PrimeVul_v0.1/` and `megavul/` into `data/raw/`
- [ ] Move `coming_data/` and `megavul_pairs/` into `data/interim/`
- [ ] Move all CSVs into `data/processed/` and `bayesian_network/` into `data/results/`
- [ ] Update all hardcoded data paths in scripts and pipeline files to reflect new `data/` layout

## Documentation
- [ ] Update `CLAUDE.md` and `docs/` to reflect new structure
- [ ] Update `Dockerfile` if it references package name or paths
