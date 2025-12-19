# Research Plan: Causal Analysis of Vulnerability-Fixing Code Changes

## Overview

**Objective**: Use Coming tool and PrimeVul dataset to analyze what types of code changes predict vulnerability fixes, employing causal inference and Bayesian hierarchical modeling.

**Core Research Questions**:
1. What AST-level change patterns (INSERT/DELETE/UPDATE operations) are causally associated with vulnerability remediation?
2. How do fix patterns vary across CWE types and software projects?
3. Can we build predictive models to identify high-risk code changes or suggest effective fixes?

**Approach**: Leverage Coming's filespair mode to extract AST-level change features from PrimeVul's paired vulnerable/patched functions, then apply causal inference methods to identify fix patterns while controlling for confounders.

## Dataset

**PrimeVul v0.1**:
- 7,578 training pairs (vulnerable/patched C/C++ functions)
- 2,623 complete pairs with both vulnerable and patched code
- 112 CWE types, 545 projects
- Top CWEs: CWE-119 (1,041), CWE-125 (790), CWE-20 (697)
- Format: JSONL with `func` (vulnerable), `target` (patched), `cwe`, `cve`, `commit_id`, `project_url`

## Tools & Environment

**Coming 6.0.0**:
- Java-based AST analysis tool
- Mode: `filespair` (direct comparison of .c/.cpp files without Git repo)
- Output: JSON with AST change operations (INSERT/DELETE/UPDATE nodes)
- Installed: `/opt/coming.jar` in Docker container

**Python Stack**:
- **Data**: pandas, numpy
- **Causal Inference**: dowhy, econml, causal-learn
- **Bayesian Modeling**: pymc, arviz
- **Environment**: Python 3.14 via uv, Docker (maven:3.9-eclipse-temurin-17)

**Existing Codebase**:
- `src/primevul_analysis/data_loader.py`: PrimeVulDataset class
- `src/primevul_analysis/patterns.py`: Regex-based PatternExtractor (fallback)
- `src/primevul_analysis/features.py`: FeatureBuilder for ML features
- `src/primevul_analysis/models.py`: BayesianFixModel skeleton
- `src/primevul_analysis/coming_extractor.py`: Planned Coming integration

## Implementation Plan

### Phase 1: Validate Coming on C/C++ Code (Week 1)

**Goal**: Test Coming's compatibility with PrimeVul's C/C++ functions and verify output quality.

**Tasks**:
1. Create test script `scripts/test_coming_filespair.py`:
   - Select 10-20 diverse examples from complete pairs
   - Write vulnerable/patched functions to temp files (.c extension)
   - Run Coming: `java -jar /opt/coming.jar -mode filespair -location1 vuln.c -location2 patched.c`
   - Parse JSON output, inspect AST change types
   
2. Validate outputs:
   - Check for parse errors, null outputs, crashes
   - Verify change operations make sense (e.g., bounds checks show as INSERT)
   - Compare Coming results to regex pattern detections
   - Document compatibility issues (unsupported C constructs, preprocessor macros)

3. Decision point:
   - **If successful**: Proceed to Phase 2 (Coming integration pipeline)
   - **If fails on most examples**: Fall back to regex patterns in `patterns.py`

**Acceptance Criteria**: Coming successfully analyzes ≥80% of test examples with meaningful AST change data.

### Phase 2: Build Coming Integration Pipeline (Week 2)

**Goal**: Automate Coming analysis for all PrimeVul pairs and extract structured features.

**Tasks**:
1. Implement `src/primevul_analysis/coming_extractor.py`:
   ```python
   class ComingExtractor:
       def analyze_pair(self, vuln_code: str, patched_code: str) -> dict
       def parse_coming_output(self, json_output: str) -> dict
       def extract_change_features(self, parsed_output: dict) -> pd.Series
   ```
   - Write temp files with unique names per pair
   - Run Coming via subprocess, capture stdout
   - Parse JSON, extract operation counts (INSERT/DELETE/UPDATE by node type)
   - Clean up temp files
   
2. Add feature extraction:
   - Count changes by AST node type (e.g., INSERT IfStatement, DELETE FunctionCall)
   - Track specific patterns (malloc→NULL check, strcpy→strncpy)
   - Compute change complexity metrics (LOC delta, AST node diff count)
   
3. Batch processing:
   - Process all 2,623 complete pairs
   - Handle errors gracefully (log failed pairs, continue)
   - Save results to `output/coming_features.parquet` for reuse
   - Estimated runtime: ~1 hour (Coming takes ~1-2s per pair)

**Deliverables**: 
- Working ComingExtractor class
- Feature dataset with ~50-100 Coming-derived features per pair
- Error analysis report (what % failed, common issues)

### Phase 3: Extract Causal Features (Week 3)

**Goal**: Transform Coming outputs into causal inference framework (treatment, confounders, outcomes).

**Tasks**:
1. Define treatment variables (binary indicators):
   - `added_null_check`: INSERT of NULL comparison in conditional
   - `added_bounds_check`: INSERT of boundary validation
   - `replaced_unsafe_func`: strcpy→strncpy, sprintf→snprintf
   - `added_error_handling`: INSERT of error return/exception
   - (10-15 treatments total, based on common fix patterns)

2. Define confounders (pre-treatment code characteristics):
   - Vulnerable code complexity: LOC, cyclomatic complexity estimate
   - Existing patterns: presence of malloc, pointer ops, string functions
   - Context: CWE type (hierarchical encoding), project ID
   - Historical: number of prior CVEs in project (if available)

3. Define outcomes (vulnerability remediation success):
   - Primary: binary indicator that patch addresses vulnerability (always 1 in PrimeVul)
   - Secondary: patch quality proxies (LOC changed, AST complexity change)
   - (Note: PrimeVul only has successful patches, so may need external data for variation)

4. Build causal DAG:
   - Document assumed causal structure: Confounders → Treatment, Confounders → Outcome, Treatment → Outcome
   - Identify potential backdoor paths to block
   - Prepare for sensitivity analysis (unmeasured confounding)

**Deliverables**:
- Feature matrix with 2,623 rows × ~80 columns (treatments, confounders, outcomes)
- Documented causal DAG (graphviz diagram)
- Data dictionary explaining each variable

### Phase 4: Pilot Causal Analysis (Week 4)

**Goal**: Run initial causal inference on subset of data to test methodology.

**Tasks**:
1. Select pilot subset:
   - Focus on top 3 CWEs (CWE-119, CWE-125, CWE-20)
   - Filter to projects with ≥5 examples
   - ~800-1000 pairs for faster iteration

2. Apply causal methods:
   - **Propensity score matching**: Estimate probability of treatment given confounders, match treated/control pairs
   - **Doubly robust estimation**: Combine outcome regression with propensity weighting
   - **Instrumental variables** (if available): Use commit metadata as instruments
   - **DoWhy framework**: Automated causal effect estimation with sensitivity analysis

3. Estimate treatment effects:
   - ATE (Average Treatment Effect): overall impact of each fix pattern
   - CATE (Conditional ATE): heterogeneous effects by CWE/project
   - Rank treatments by effect size and statistical significance

4. Validation:
   - Placebo tests: randomize treatments, verify null effects
   - Refutation tests: add random confounder, check robustness
   - Cross-validation: train/test split for causal model performance

**Deliverables**:
- Pilot analysis report with causal effect estimates for top 5 fix patterns
- Sensitivity analysis results (how robust are findings to unmeasured confounding?)
- Lessons learned document (what worked, what needs refinement)

### Phase 5: Scale to Full Dataset with Bayesian Models (Week 5-6)

**Goal**: Run hierarchical Bayesian models on full 2,623 pairs to capture heterogeneous effects.

**Tasks**:
1. Implement Bayesian hierarchical model in PyMC:
   ```python
   with pm.Model() as model:
       # Hyperpriors for CWE/project variance
       sigma_cwe = pm.HalfNormal('sigma_cwe', sigma=1)
       sigma_project = pm.HalfNormal('sigma_project', sigma=1)
       
       # Random effects
       cwe_effect = pm.Normal('cwe_effect', mu=0, sigma=sigma_cwe, shape=n_cwes)
       project_effect = pm.Normal('project_effect', mu=0, sigma=sigma_project, shape=n_projects)
       
       # Treatment effects (one per fix pattern)
       treatment_effect = pm.Normal('treatment_effect', mu=0, sigma=1, shape=n_treatments)
       
       # Linear predictor
       mu = (treatment_effect[treatment_idx] + 
             cwe_effect[cwe_idx] + 
             project_effect[project_idx] +
             confounder_coeffs @ confounders.T)
       
       # Likelihood
       outcome = pm.Bernoulli('outcome', logit_p=mu, observed=y)
       
       # Sample posterior
       trace = pm.sample(2000, tune=1000, chains=4)
   ```

2. Model fitting:
   - Separate models for different outcome variables
   - MCMC diagnostics: Rhat, effective sample size, trace plots
   - Posterior predictive checks: compare observed vs. simulated data

3. Posterior analysis:
   - Extract credible intervals for treatment effects
   - Compute probability that each treatment reduces vulnerability risk
   - Identify CWEs/projects with strongest/weakest treatment effects
   - Generate rankings of fix patterns by expected impact

4. Visualization:
   - Forest plots of treatment effects with uncertainty
   - Heatmaps of CATE across CWE × treatment combinations
   - Network diagram of fix pattern co-occurrence
   - Interactive dashboard (Streamlit/Plotly) for exploring results

**Deliverables**:
- Trained Bayesian models (saved as `.nc` files via ArviZ)
- Full analysis report with posterior summaries
- Publication-ready figures
- Interactive dashboard for stakeholders

## Research Questions to Answer

1. **Pattern Effectiveness**:
   - Which fix patterns have largest causal effects on vulnerability remediation?
   - Are there patterns with negative effects (making code worse)?
   - Do combined patterns (e.g., bounds check + null check) have synergistic effects?

2. **Heterogeneous Effects**:
   - How do fix patterns vary by CWE type? (e.g., CWE-119 vs CWE-125)
   - Which projects show different fix effectiveness? (domain-specific patterns?)
   - Are there interaction effects (pattern X works for CWE Y but not Z)?

3. **Predictive Modeling**:
   - Can we predict fix success from pre-patch code characteristics?
   - Given a vulnerability, what fix patterns are most likely to work?
   - Can we generate fix recommendations based on causal model?

4. **Confounding Analysis**:
   - How much does code complexity confound treatment effects?
   - Are there unmeasured confounders (e.g., developer expertise)?
   - Sensitivity: how strong would unmeasured confounding need to be to invalidate findings?

## Assumptions & Limitations

**Assumptions**:
- Coming correctly parses C/C++ code in PrimeVul (validate in Phase 1)
- Paired functions represent true vulnerable/patched versions (trust dataset curation)
- AST changes capture meaningful semantic differences (not just whitespace/comments)
- Causal DAG is correctly specified (no unmeasured confounding of treatment→outcome)

**Limitations**:
- PrimeVul only has successful patches (no negative examples of failed fixes)
- Limited to C/C++ vulnerabilities (may not generalize to other languages)
- Coming output quality depends on code complexity (may fail on obfuscated/macros)
- Sample size varies by CWE (some rare CWEs have <10 examples)
- No temporal data (can't analyze evolution of fix patterns over time)

**Mitigation Strategies**:
- Phase 1 validation catches Coming compatibility issues early
- Regex fallback if Coming fails
- Sensitivity analysis addresses unmeasured confounding
- Hierarchical models handle sparse CWEs via partial pooling
- Document limitations clearly in final report

## Timeline

- **Week 1**: Phase 1 (Coming validation)
- **Week 2**: Phase 2 (Coming pipeline)
- **Week 3**: Phase 3 (Causal features)
- **Week 4**: Phase 4 (Pilot analysis)
- **Week 5-6**: Phase 5 (Bayesian models)
- **Week 7**: Writing & documentation

**Total**: ~7 weeks for complete pipeline from validation to final models

## Next Steps

1. **Immediate**: Create `scripts/test_coming_filespair.py` to run Phase 1 validation
2. Read Coming documentation for filespair mode details
3. Review econml/DoWhy tutorials for causal inference methodology
4. Set up logging/error handling for long-running Coming batch jobs