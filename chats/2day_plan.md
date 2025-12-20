# 2-Day Research Sprint Plan

## Goal

Produce an analysis report on vulnerability fix patterns from PrimeVul dataset using Coming tool and causal inference methods.

## Timeline

**Day 1**: Feature extraction pipeline  
**Day 2**: Analysis and report

## Key Milestones

### Day 1: Feature Extraction
- Validate Coming on sample of PrimeVul data (pivot to regex fallback if needed)
- Extract features from full dataset (~2,600 pairs)
- Generate treatment/confounder feature matrix
- Produce descriptive statistics and visualizations

### Day 2: Analysis & Report
- Run simplified causal analysis on top CWEs
- Generate key findings and effect estimates
- Write report with results and figures

## Research Questions

1. What are the most common fix patterns for top CWEs?
2. Do certain patterns strongly associate with specific vulnerability types?
3. What's the relationship between code complexity and fix pattern choices?

## Deliverables

**Code & Data**:
- Feature extraction pipeline
- Processed feature datasets
- Analysis notebooks

**Report** (8-12 pages):
- Methods: feature extraction approach, statistical methods
- Results: descriptive statistics, causal analysis findings
- Discussion: interpretations, limitations, future work

## Scope

**Focus on**:
- Top 3-5 CWEs with most examples
- Simple causal methods (logistic regression, propensity scores)
- Core fix patterns (bounds checks, null checks, unsafe function replacements)

**Defer for future**:
- Bayesian hierarchical models
- Extensive sensitivity analysis
- Analysis of all 112 CWE types
- Interactive dashboards

## Success Criteria

- Features extracted from ≥80% of dataset
- 5-10 key patterns identified and analyzed
- Causal analysis completed for top CWEs
- Complete report with methodology and findings
