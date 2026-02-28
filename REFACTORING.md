# Refactoring Plan

## Package & Imports
- [x] Rename package directory `src/primevul_analysis/` → `src/diff_analysis/` and update `pyproject.toml` project name to `diff-analysis`
- [x] Update all imports from `primevul_analysis` → `diff_analysis` across every Python file

## Module Cleanup
- [x] Rename `datapreparator/extract.py` → `datapreparator/primevul.py`
- [x] Remove dead modules: `config/settings.py` and `logging/logger.py`

## Scripts
- [x] Group scripts by dataset: `scripts/primevul/` and `scripts/megavul/`
- [ ] Move docker/utility scripts into project root `scripts/` (fix-permissions.sh, etc.)

## Pipeline Entry Points
- [x] Create `pipeline_primevul.py` at project root (move logic from `__main__.py`)
- [x] Create `pipeline_megavul.py` at project root
- [x] Make `__main__.py` a minimal dispatcher (`python -m diff_analysis primevul/megavul`)

## Data Directory
- [x] Restructure `data/`: `raw/`, `interim/`, `processed/`, `results/` layout reflected in all code paths
- [x] Move `megavul/` → `data/raw/megavul/` (git mv + .gitattributes updated)
- [ ] Move `PrimeVul_v0.1/` → `data/raw/PrimeVul_v0.1/` (gitignored; move manually on each machine)
- [x] Update all hardcoded data paths in scripts and pipeline files

## Documentation
- [x] Update `CLAUDE.md` to reflect new structure
- [ ] Update `Dockerfile` if it references package name or paths
