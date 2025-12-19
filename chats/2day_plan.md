# Rapid 2-Day MPU Project Plan

## Overview

**Timeline**: 2 days to complete feature extraction, basic causal analysis, and report
**Goal**: Produce a report with basic results on vulnerability fix patterns from PrimeVul dataset
**Approach**: Streamlined pipeline focusing on actionable insights over methodological sophistication

## Day 1 (8 hours): Coming Validation & Feature Extraction

### Morning (4 hours)

#### 1. Quick Coming Test (1 hour)
- Test Coming on 5-10 examples from different CWEs
- **Decision point**: 
  - If >50% work: proceed with Coming
  - If <50% work: pivot to regex patterns only

**Tasks:**
- Create `scripts/quick_coming_test.py`
- Select diverse examples (different CWEs, LOC sizes)
- Run Coming in filespair mode
- Document success rate and issues

#### 2. Feature Extraction (3 hours)
- **If Coming works**: Run batch processing on all 2,623 pairs
- **If Coming fails**: Use existing `patterns.py` for regex-based features
- Handle errors gracefully (log failures, continue)
- Save results to `output/features.parquet`

**Tasks:**
- Create `scripts/extract_all_features.py`
- Implement parallel processing if possible
- Track progress and errors
- Validate output quality

### Afternoon (4 hours)

#### 3. Basic Feature Engineering (2 hours)
**Treatment Variables** (5-10 binary indicators):
- `added_null_check`: Added NULL pointer validation
- `added_bounds_check`: Added array/buffer bounds validation
- `replaced_unsafe_func`: Replaced strcpy→strncpy, sprintf→snprintf
- `added_size_check`: Added size/length validation
- `added_error_handling`: Added error return/exception handling

**Confounders**:
- LOC (lines of code in vulnerable function)
- CWE type (categorical)
- Function complexity proxies (number of conditionals, loops)
- Project ID (categorical)

**Tasks:**
- Create feature engineering script
- Generate treatment/confounder matrix
- Check for missing values, outliers
- Save processed dataset

#### 4. Descriptive Statistics (2 hours)
**Analysis:**
- Distribution of fix patterns by CWE
- Co-occurrence matrix of fix patterns
- Basic correlations between patterns and confounders
- Summary statistics tables

**Visualizations:**
- Bar charts: pattern frequency by top CWEs
- Heatmap: pattern co-occurrence
- Scatter plots: LOC vs pattern complexity
- Distribution plots: key variables

**Tasks:**
- Create `notebooks/descriptive_analysis.ipynb`
- Generate tables and figures
- Save plots to `figures/descriptive/`

## Day 2 (8 hours): Simple Causal Analysis & Report

### Morning (4 hours)

#### 5. Simplified Causal Analysis (3 hours)
**Focus**: Top 3 CWEs only (CWE-119, CWE-125, CWE-20) → ~1,500 pairs

**Methods** (skip Bayesian models):
1. **Logistic Regression with Fixed Effects**
   - Outcome: patch success (always 1, so use pattern presence as outcome)
   - Predictors: confounders + CWE fixed effects
   - Estimate odds ratios for each pattern

2. **Propensity Score Matching** (via DoWhy)
   - Match treated/control pairs on confounders
   - Estimate average treatment effect (ATE)
   - Simple API: `model.identify_effect()`, `model.estimate_effect()`

3. **Chi-Square Tests**
   - Test independence of patterns and CWE types
   - Identify significant pattern-CWE associations

**Tasks:**
- Create `notebooks/causal_analysis.ipynb`
- Run analyses for each treatment variable
- Generate effect size estimates and p-values
- Focus on interpretability over sophistication

#### 6. Key Results (1 hour)
**Compile findings:**
- Which patterns are most common for each CWE?
- Odds ratios for fix pattern associations
- Pattern combinations that frequently co-occur
- 3-5 key takeaways

**Tasks:**
- Create results summary table
- Extract top findings
- Prepare for report writing

### Afternoon (4 hours)

#### 7. Report Writing (4 hours)
**Structure** (8-12 pages):

1. **Introduction** (1 page)
   - Problem: Understanding vulnerability fix patterns
   - Dataset: PrimeVul v0.1 (2,623 pairs, 112 CWEs)
   - Research questions (simplified)
   - Approach overview

2. **Methods** (2 pages)
   - Data: PrimeVul dataset description
   - Feature extraction: Coming/regex approach
   - Treatment/confounder variables
   - Statistical methods: logistic regression, propensity scores
   - Scope: top 3 CWEs

3. **Results** (3-4 pages)
   - Descriptive statistics (tables + 3-4 figures)
   - Pattern distributions by CWE (bar charts, heatmaps)
   - Causal analysis results (effect estimates table)
   - Key findings (3-5 bullet points with evidence)

4. **Discussion** (2-3 pages)
   - Interpretation of findings
   - Practical implications for developers
   - Limitations (clear and honest)
   - Future work directions

5. **Conclusion** (0.5 page)
   - Summary of contributions
   - Final takeaways

**Tasks:**
- Write in LaTeX or Markdown
- Include 5-10 figures/tables
- Cite Coming, PrimeVul, DoWhy
- Export to PDF

## Minimal Viable Deliverables

### Code
- `scripts/quick_coming_test.py` - Coming validation script
- `scripts/extract_all_features.py` - Batch feature extraction
- `notebooks/descriptive_analysis.ipynb` - Exploratory analysis
- `notebooks/causal_analysis.ipynb` - Simple causal inference

### Data Outputs
- `output/features.parquet` - Extracted features from all pairs
- `output/processed_data.parquet` - Treatment/confounder matrix
- `output/results.csv` - Treatment effect estimates

### Figures
- `figures/descriptive/` - Descriptive statistics plots (5-6 figures)
- `figures/causal/` - Causal analysis plots (3-4 figures)

### Report
- `report.pdf` or `report.md` - Final 8-12 page report

## Scope Cuts from Original Plan

### ❌ Cut (save for future work)
- Bayesian hierarchical models (too time-intensive)
- Analysis of all 112 CWEs (focus on top 3-5)
- Instrumental variables approach
- Extensive sensitivity analysis
- Refutation tests and placebo tests
- Interactive dashboard
- Multiple outcome variables
- Temporal analysis

### ✅ Keep (essential for 2-day timeline)
- Coming validation (with 1-hour pivot option)
- Feature extraction on full dataset
- Descriptive statistics (distributions, correlations)
- Simple causal inference (logistic + propensity scores)
- Focus on top CWEs
- Clear reporting of limitations
- Basic visualizations

## Research Questions (Simplified)

1. **Pattern Prevalence**: What are the most common fix patterns for top CWEs?
2. **Pattern-CWE Associations**: Do certain patterns strongly associate with specific vulnerability types?
3. **Confounding**: What's the relationship between code complexity and fix pattern choices?

## Key Assumptions

1. Coming can parse most PrimeVul C/C++ code (validate in first hour)
2. Regex patterns can serve as fallback if Coming fails
3. Pattern presence indicates fix relevance (even though all patches succeed)
4. Simple causal methods provide meaningful insights for exploratory analysis
5. Focusing on top CWEs captures majority of patterns

## Risk Mitigation

**Risk**: Coming fails on most examples
- **Mitigation**: Pivot to regex patterns after 1 hour

**Risk**: Feature extraction takes too long
- **Mitigation**: Process subset if needed, document limitations

**Risk**: No significant causal findings
- **Mitigation**: Report descriptive findings, reframe as exploratory study

**Risk**: Report writing takes longer than expected
- **Mitigation**: Use existing notebooks as report sections, minimal polish

## Success Criteria

**Minimum viable outcome:**
- ✅ Features extracted from ≥80% of dataset
- ✅ 5-10 key patterns identified and analyzed
- ✅ Basic causal analysis completed for top 3 CWEs
- ✅ 8-12 page report with results and figures
- ✅ Clear documentation of methodology and limitations

**Bonus (if time permits):**
- Analysis of top 5 CWEs instead of 3
- More sophisticated causal methods (doubly robust estimation)
- Additional visualizations
- Code documentation and README

## Timeline Summary

| Time | Task | Deliverable |
|------|------|-------------|
| Day 1 AM | Coming test + feature extraction | `features.parquet` |
| Day 1 PM | Feature engineering + descriptives | Processed data + figures |
| Day 2 AM | Causal analysis | Results tables + plots |
| Day 2 PM | Report writing | Final report PDF |

## Next Immediate Steps

1. **RIGHT NOW**: Create `scripts/quick_coming_test.py`
2. Select 5-10 test examples from PrimeVul
3. Run Coming validation
4. Make pivot decision (Coming vs regex)
5. Begin batch feature extraction
