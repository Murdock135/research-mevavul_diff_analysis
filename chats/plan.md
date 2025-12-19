Read [](file:///home/zayan/Documents/code/mine/research-lm_error_analysis/src/primevul_analysis)

Read [](file:///home/zayan/Documents/code/mine/research-lm_error_analysis/src/primevul_analysis/__init__.py)

Read [](file:///home/zayan/Documents/code/mine/research-lm_error_analysis/pyproject.toml)

## Plan: Implement Pattern Extraction and Modeling Pipeline

Build a modular pipeline in primevul_analysis to extract fix patterns from PrimeVul data, create feature matrices, and prepare for Bayesian modeling. Start with core data loading and pattern detection, then progress to feature engineering and model preparation.

### Steps

1. **Create core data loading module** - Add data_loader.py with `PrimeVulDataset` class that loads JSONL files (train/valid/test), provides methods to filter by CWE/project, handles paired structure (consecutive vulnerable/fixed), exposes convenient accessors for metadata (project counts, CWE distribution). Add config.py for paths and constants.

2. **Build pattern extraction module** - Create patterns.py with `PatternExtractor` class that implements the regex patterns from your exploration (vulnerable patterns: `unsafe_strcpy`, `malloc_call`, `pointer_deref`; fix patterns: `bounds_check`, `null_check`, `safe_snprintf`), adds new transformation detectors (`strcpy→strncpy`, `sprintf→snprintf`), computes pattern co-occurrence matrices, provides diff-based metrics (LOC delta, lines added/deleted). Make it extensible for adding patterns.

3. **Implement feature engineering pipeline** - Add features.py with `FeatureBuilder` class that takes dataset + pattern extractor, creates binary treatment variables (one column per fix pattern), extracts code characteristics (LOC, pattern counts in vulnerable code), builds hierarchical grouping variables (CWE encoding, project encoding), outputs pandas DataFrame ready for modeling with clear train/validation splits.

4. **Setup Bayesian modeling infrastructure** - Update pyproject.toml to add `pymc>=5.0`, `arviz>=0.18`, `graphviz>=0.20` for visualization, create models.py with `BayesianFixModel` class skeleton that specifies hierarchical structure (CWE/project random effects), includes methods for fitting, posterior sampling, and generating predictions with uncertainty.

5. **Add analysis and visualization utilities** - Create analysis.py for posterior analysis (credible intervals, probability tables, pattern frequency by CWE/project), add viz.py for plotting (fix pattern distributions, heatmaps of patterns×CWE, posterior predictive checks), build notebook template in notebooks that demonstrates full pipeline (load→extract→model→analyze).

### Further Considerations

1. **Module organization?** Flat structure with all modules in primevul_analysis (Option A—simple, good for prototyping), or nested structure with subpackages like `data/`, `patterns/`, `models/` (Option B—better for larger codebase), or functional approach with separate scripts (Option C—less reusable)?

2. **Testing strategy?** Write unit tests immediately for each module (Option A—slower but robust), add tests after prototype works (Option B—faster iteration), or skip formal tests for research code (Option C—risky but common in academia)?

3. **Start with which module?** Begin with data_loader + config to establish foundation (Option A—systematic), start with patterns.py since you have regex already (Option B—quick wins), or build end-to-end minimal example first then refactor (Option C—validates approach early)?