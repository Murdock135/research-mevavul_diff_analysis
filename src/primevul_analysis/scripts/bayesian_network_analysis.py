# src/primevul_analysis/scripts/bayesian_network_analysis.py

from __future__ import annotations

from pathlib import Path
import logging
import numpy as np
import pandas as pd

import networkx as nx
import matplotlib.pyplot as plt

from pgmpy.estimators import HillClimbSearch
from pgmpy.models import DiscreteBayesianNetwork as BayesianNetwork
from pgmpy.estimators import MaximumLikelihoodEstimator

from sklearn.feature_selection import mutual_info_classif

from primevul_analysis.utils.config_utils import find_project_root

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
)
logger = logging.getLogger(__name__)


# -------------------------
# Helpers
# -------------------------

def make_binary_target(df: pd.DataFrame, *, cwe_of_interest: str) -> pd.Series:
    """Y = 1 if primary_cwe == cwe_of_interest else 0."""
    if "primary_cwe" not in df.columns:
        raise ValueError("Missing column 'primary_cwe' in input CSV.")
    return (df["primary_cwe"].astype(str) == str(cwe_of_interest)).astype(int)


def keep_existing_columns(df: pd.DataFrame, cols: list[str], *, name: str = "bn_features") -> list[str]:
    """Filter a list of columns to only those present in df; log missing."""
    existing = [c for c in cols if c in df.columns]
    missing = [c for c in cols if c not in df.columns]

    if missing:
        logger.warning("%s: dropping %d missing columns: %s", name, len(missing), missing)

    if not existing:
        raise ValueError(f"{name} is empty after filtering. Check your column names.")

    return existing


def select_top_k_features_by_mi(
    X: pd.DataFrame, y: pd.Series, k: int = 40, random_state: int = 0
) -> list[str]:
    """Fast filter to keep BN learning tractable."""
    X_num = X.apply(pd.to_numeric, errors="coerce").fillna(0)

    # Treat as discrete: our inputs are already counts/flags
    mi = mutual_info_classif(
        X_num.values,
        y.values,
        discrete_features=True,
        random_state=random_state,
    )
    mi_series = pd.Series(mi, index=X_num.columns).sort_values(ascending=False)

    logger.info("Top MI features (first 15):\n%s", mi_series.head(15).to_string())
    return mi_series.head(k).index.tolist()


def discretize_counts(series: pd.Series, *, mode: str = "binary_or_2plus") -> pd.Series:
    """
    Convert a numeric count/flag into small discrete states.
      - 'binary'          : 0 vs >0
      - 'binary_or_2plus' : 0 vs 1 vs 2+
      - 'quantile_4'      : quartiles
    """
    s = pd.to_numeric(series, errors="coerce").fillna(0)

    if mode == "binary":
        return (s > 0).astype(int)

    if mode == "binary_or_2plus":
        # 0 -> 0, 1 -> 1, >=2 -> 2
        return pd.Series(np.where(s <= 0, 0, np.where(s == 1, 1, 2)), index=s.index, dtype=int)

    if mode == "quantile_4":
        if s.nunique(dropna=False) <= 1:
            return pd.Series(0, index=s.index, dtype=int)
        binned = pd.qcut(s, q=4, labels=False, duplicates="drop")
        return binned.fillna(0).astype(int)

    raise ValueError(f"Unknown discretization mode: {mode}")


def build_bn_dataframe(
    df: pd.DataFrame,
    feature_cols: list[str],
    target_col: str,
    *,
    discretize_mode: str = "binary_or_2plus",
) -> pd.DataFrame:
    """Make a fully-discrete dataframe for pgmpy."""
    out = pd.DataFrame(index=df.index)

    for col in feature_cols:
        out[col] = discretize_counts(df[col], mode=discretize_mode)

    out[target_col] = df[target_col].astype(int)

    # Drop constant cols (pgmpy + scoring can behave weird with constants)
    nunique = out.nunique(dropna=False)
    constant_cols = nunique[nunique <= 1].index.tolist()
    if constant_cols:
        logger.info("Dropping constant columns: %s", constant_cols)
        out = out.drop(columns=constant_cols)

    # Enforce integer discrete states
    for c in out.columns:
        out[c] = out[c].astype(int)

    return out


def draw_bn(model: BayesianNetwork, out_path: Path) -> None:
    plt.figure(figsize=(14, 10))
    G = nx.DiGraph()
    G.add_nodes_from(model.nodes())
    G.add_edges_from(model.edges())

    pos = nx.spring_layout(G, seed=0, k=1.2)

    nx.draw(
        G,
        pos,
        with_labels=True,
        node_size=1400,
        font_size=8,
        arrowsize=18,
    )
    plt.title("Learned Bayesian Network Structure")
    out_path.parent.mkdir(parents=True, exist_ok=True)
    plt.savefig(out_path, dpi=300, bbox_inches="tight")
    plt.close()
    logger.info("Saved BN graph to %s", out_path)


# -------------------------
# Main
# -------------------------

def main():
    project_root = find_project_root()
    data_path = project_root / "data" / "features_with_labels.csv"

    out_dir = project_root / "data" / "bayesian_network"
    out_png = out_dir / "bn_structure.png"
    out_edges = out_dir / "bn_edges.csv"
    out_used_cols = out_dir / "bn_used_columns.txt"

    logger.info("Project root: %s", project_root)
    logger.info("Loading merged dataset: %s", data_path)
    df = pd.read_csv(data_path)
    logger.info("Loaded %d rows, %d columns", len(df), len(df.columns))

    # -------------------------
    # Target Y
    # -------------------------
    cwe_of_interest = "CWE-787"  # CHANGE THIS
    df["Y"] = make_binary_target(df, cwe_of_interest=cwe_of_interest)
    logger.info("Y=1 count: %d / %d", int(df["Y"].sum()), len(df))

    # -------------------------
    # Candidate feature columns
    # -------------------------
    # Only columns from your GumTree feature set
    feature_prefixes = ("n_", "node_", "act_", "touches_", "deleted_", "inserted_", "ratio_", "has_")
    candidate_cols = [c for c in df.columns if c.startswith(feature_prefixes)]
    logger.info("Found %d candidate feature columns by prefix", len(candidate_cols))

    if not candidate_cols:
        raise ValueError("No candidate feature columns found. Check your prefixes / CSV columns.")

    # -------------------------
    # Option A: manual short list (safe filtered)
    # -------------------------
    manual_bn_features = [
        "touches_sensitive_ast",
        "touches_control_flow",
        "touches_if",
        "touches_loop",
        "touches_return",
        "n_delete_node",
        "n_delete_tree",
        "n_insert_node",
        "n_move_tree",
        "n_updated_labels",
        "has_safety_token_update",
    ]
    manual_bn_features = keep_existing_columns(df, manual_bn_features, name="manual_bn_features")

    # Decide whether to use manual list or MI selection
    USE_MANUAL = False  # set True if you want exactly the manual list
    if USE_MANUAL:
        bn_features = manual_bn_features
        logger.info("Using MANUAL feature list (%d features)", len(bn_features))
    else:
        # -------------------------
        # Option B: MI top-K (recommended)
        # -------------------------
        X = df[candidate_cols].copy()
        TOP_K = 40  # CHANGE (20–60 is typical)
        top_cols = select_top_k_features_by_mi(X, df["Y"], k=TOP_K)
        bn_features = keep_existing_columns(df, top_cols, name="mi_topk_features")
        logger.info("Using MI top-K feature list (%d features)", len(bn_features))

    # Save which columns we actually used (debugging / reproducibility)
    out_dir.mkdir(parents=True, exist_ok=True)
    with out_used_cols.open("w", encoding="utf-8") as f:
        for c in bn_features + ["Y"]:
            f.write(c + "\n")
    logger.info("Saved used columns list to %s", out_used_cols)

    # -------------------------
    # Build discrete BN dataset
    # -------------------------
    bn_df = build_bn_dataframe(df, bn_features, "Y", discretize_mode="binary_or_2plus")
    logger.info("BN dataset shape after discretization: %s", bn_df.shape)

    # -------------------------
    # Structure learning
    # -------------------------
    logger.info("Running HillClimbSearch + BIC...")
    hc = HillClimbSearch(bn_df)
    learned_dag = hc.estimate(scoring_method="bic-d")  # discrete BIC
    edges = list(learned_dag.edges())
    logger.info("Learned %d edges", len(edges))

    pd.DataFrame(edges, columns=["src", "dst"]).to_csv(out_edges, index=False)
    logger.info("Saved edges to %s", out_edges)

    # -------------------------
    # Parameter learning
    # -------------------------
    bn = BayesianNetwork(edges)
    bn.fit(bn_df, estimator=MaximumLikelihoodEstimator)

    logger.info("Learned CPDs (showing up to 10):")
    for i, cpd in enumerate(bn.get_cpds()):
        if i >= 10:
            break
        logger.info("\n%s", cpd)

    # -------------------------
    # Visualization
    # -------------------------
    draw_bn(bn, out_png)


if __name__ == "__main__":
    main()
