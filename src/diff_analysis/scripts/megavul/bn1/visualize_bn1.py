"""
Generate BN1 paper figures and tables.

Outputs
-------
data/results/figures/
  bn1_f1_dag_full.{png,pdf}       Full learned DAG (all 51 nodes)
  bn1_f1_dag_ego.{png,pdf}        is_vul 1-hop ego graph
  bn1_f2_heatmap.{png,pdf}        P(is_vul=1) over all parent state combinations
  bn1_f3_mi_bar.{png,pdf}         MI bar chart — top-20 features coloured by action type

data/results/tables/
  bn1_t1_dataset.csv              Dataset summary statistics
  bn1_t2_mi_top20.csv             Top-20 features by MI with is_vul
  bn1_t3_structure.csv            BN edge/node summary
  bn1_t4_lift.csv                 P(is_vul=1 | parent=1, others=0) — lift per parent
"""

import itertools
import logging
import pickle
from pathlib import Path

import matplotlib.patches as mpatches
import matplotlib.pyplot as plt
import networkx as nx
import numpy as np
import pandas as pd
import seaborn as sns
from pgmpy.inference import VariableElimination
from sklearn.feature_selection import mutual_info_classif

from diff_analysis.scripts.megavul.bn_utils import META_COLS, BNPipeline
from diff_analysis.utils.config_utils import find_project_root
from diff_analysis.utils.logging import setup_logging

setup_logging(level=logging.INFO)
logger = logging.getLogger(__name__)

TARGET_COL = "is_vul"

ACTION_PREFIXES = {
    "delete-node": "dn",
    "insert-node": "in",
    "move-tree":   "mt",
    "update-node": "un",
}
ACTION_COLORS = {
    "delete-node": "#e74c3c",
    "insert-node": "#2ecc71",
    "move-tree":   "#3498db",
    "update-node": "#f39c12",
}
DEFAULT_COLOR  = "#95a5a6"
TARGET_COLOR   = "#8e44ad"
EDGE_INTO_TGT  = "#27ae60"   # feature → is_vul
EDGE_FROM_TGT  = "#e67e22"   # is_vul → feature
EDGE_OTHER     = "#bdc3c7"


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def shorten(name: str) -> str:
    for full, abbr in ACTION_PREFIXES.items():
        if name.startswith(full + "_"):
            return abbr + "_" + name[len(full) + 1:]
    return name


def action_type(name: str) -> str:
    for prefix in ACTION_PREFIXES:
        if name.startswith(prefix + "_"):
            return prefix
    return "other"


def node_color(name: str) -> str:
    if name == TARGET_COL:
        return TARGET_COLOR
    return ACTION_COLORS.get(action_type(name), DEFAULT_COLOR)


def save_fig(fig: plt.Figure, out_dir: Path, stem: str) -> None:
    out_dir.mkdir(parents=True, exist_ok=True)
    for ext in ("png", "pdf"):
        fig.savefig(out_dir / f"{stem}.{ext}", dpi=300, bbox_inches="tight")
    logger.info(f"Saved → {out_dir / stem}.{{png,pdf}}")


def save_table(df: pd.DataFrame, out_dir: Path, stem: str) -> None:
    out_dir.mkdir(parents=True, exist_ok=True)
    df.to_csv(out_dir / f"{stem}.csv", index=False)
    logger.info(f"Saved → {out_dir / stem}.csv")


# ---------------------------------------------------------------------------
# T1: Dataset summary
# ---------------------------------------------------------------------------

def table_dataset_summary(df_raw: pd.DataFrame, target_col: str) -> pd.DataFrame:
    feature_cols = [c for c in df_raw.columns if c not in META_COLS]
    X = df_raw[feature_cols]
    sparsity = (X == 0).values.mean() * 100
    n_vul = int(df_raw[target_col].astype(int).sum())
    rows = [
        ("Total rows",                  len(df_raw)),
        ("Vulnerable functions",        n_vul),
        ("Patched functions",           len(df_raw) - n_vul),
        ("Unique CVEs",                 df_raw["cve_id"].nunique()),
        ("Unique commits",              df_raw["commit_hash"].nunique()),
        ("Unique repos",                df_raw["repo_name"].nunique()),
        ("Feature columns",             len(feature_cols)),
        ("Feature matrix sparsity (%)", f"{sparsity:.1f}"),
    ]
    return pd.DataFrame(rows, columns=["Metric", "Value"])


# ---------------------------------------------------------------------------
# T2 + F3: MI scores
# ---------------------------------------------------------------------------

def compute_mi_series(df_raw: pd.DataFrame, target_col: str) -> pd.Series:
    feature_cols = [c for c in df_raw.columns if c not in META_COLS]
    X = df_raw[feature_cols].values
    y = df_raw[target_col].astype(int).values
    mi = mutual_info_classif(X, y, discrete_features=True, random_state=0)
    return pd.Series(mi, index=feature_cols).sort_values(ascending=False)


def table_mi_top20(mi_series: pd.Series) -> pd.DataFrame:
    top20 = mi_series.head(20).reset_index()
    top20.columns = ["Feature", "MI Score"]
    top20.insert(0, "Rank", range(1, 21))
    top20["Action"] = top20["Feature"].apply(action_type)
    top20["MI Score"] = top20["MI Score"].round(5)
    return top20[["Rank", "Feature", "Action", "MI Score"]]


# ---------------------------------------------------------------------------
# T3: BN structure summary
# ---------------------------------------------------------------------------

def table_structure_summary(pipeline: BNPipeline, target_col: str) -> pd.DataFrame:
    edges = pipeline.edges
    par   = [(u, v) for u, v in edges if v == target_col]
    ch    = [(u, v) for u, v in edges if u == target_col]
    other = [(u, v) for u, v in edges if target_col not in (u, v)]
    nodes = set(n for e in edges for n in e)
    rows = [
        ("Total nodes",                              len(nodes)),
        ("Total edges",                              len(edges)),
        (f"Feature → {target_col}  (parent edges)", len(par)),
        (f"{target_col} → feature  (child edges)",  len(ch)),
        ("Feature ↔ feature  (other edges)",        len(other)),
    ]
    return pd.DataFrame(rows, columns=["Metric", "Count"])


# ---------------------------------------------------------------------------
# T4: Lift table  P(is_vul=1 | one parent=1, rest=0)
# ---------------------------------------------------------------------------

def table_lift(bn, target_col: str) -> pd.DataFrame:
    ve = VariableElimination(bn)
    parents = list(bn.get_parents(target_col))
    vul_idx = list(bn.get_cpds(target_col).state_names[target_col]).index("1")

    def p_vul(evidence: dict) -> float:
        q = ve.query([target_col], evidence=evidence, show_progress=False)
        return float(q.values[vul_idx])

    baseline = p_vul({p: "0" for p in parents})
    rows = [{
        "Condition":    "Baseline (all parents = 0)",
        "P(is_vul=1)": round(baseline, 4),
        "Lift":         1.000,
    }]
    for parent in parents:
        ev = {p: "0" for p in parents}
        ev[parent] = "1"
        pv = p_vul(ev)
        rows.append({
            "Condition":    f"{parent} = 1",
            "P(is_vul=1)": round(pv, 4),
            "Lift":         round(pv / baseline, 3) if baseline > 0 else float("inf"),
        })
    return pd.DataFrame(rows)


# ---------------------------------------------------------------------------
# F1a: Full DAG
# ---------------------------------------------------------------------------

def _hierarchical_pos(G: nx.DiGraph) -> dict:
    """Longest-path layering → multipartite layout."""
    layers: dict[str, int] = {}
    for node in nx.topological_sort(G):
        preds = list(G.predecessors(node))
        layers[node] = max((layers[p] + 1 for p in preds), default=0)
    by_layer: dict[int, list] = {}
    for node, lyr in layers.items():
        by_layer.setdefault(lyr, []).append(node)
    pos = {}
    for lyr, nodes in by_layer.items():
        for i, node in enumerate(nodes):
            x = (i - (len(nodes) - 1) / 2) * 2.2
            pos[node] = (x, -lyr * 2.5)
    return pos


def figure_dag_full(pipeline: BNPipeline, figures_dir: Path, *, stem: str = "bn1") -> None:
    G = nx.DiGraph()
    G.add_edges_from(pipeline.edges)

    try:
        pos = _hierarchical_pos(G)
    except Exception:
        pos = nx.spring_layout(G, seed=42)

    colors = [node_color(n) for n in G.nodes()]
    labels = {n: (n if n == TARGET_COL else shorten(n)) for n in G.nodes()}
    edge_colors = [
        EDGE_INTO_TGT if v == TARGET_COL else
        EDGE_FROM_TGT if u == TARGET_COL else
        EDGE_OTHER
        for u, v in G.edges()
    ]

    fig, ax = plt.subplots(figsize=(22, 14))
    nx.draw_networkx(
        G, pos=pos, ax=ax, labels=labels,
        node_color=colors, node_size=900,
        edge_color=edge_colors, arrows=True, arrowsize=10,
        font_size=5.5, font_weight="bold",
        connectionstyle="arc3,rad=0.05",
    )
    legend_handles = [
        mpatches.Patch(color=c, label=a) for a, c in ACTION_COLORS.items()
    ] + [
        mpatches.Patch(color=TARGET_COLOR, label=f"{TARGET_COL} (target)"),
        mpatches.Patch(color=EDGE_INTO_TGT, label=f"feature → {TARGET_COL}"),
        mpatches.Patch(color=EDGE_FROM_TGT, label=f"{TARGET_COL} → feature"),
    ]
    ax.legend(handles=legend_handles, loc="upper right", fontsize=8)
    ax.set_title("BN1: Learned DAG — Code Changes → Vulnerability", fontsize=14)
    ax.axis("off")
    plt.tight_layout()
    save_fig(fig, figures_dir / "dag_full", f"{stem}_dag_full")
    plt.close(fig)


# ---------------------------------------------------------------------------
# F1b: Ego graph (1-hop neighbourhood of is_vul)
# ---------------------------------------------------------------------------

def figure_dag_ego(pipeline: BNPipeline, figures_dir: Path, *, stem: str = "bn1") -> None:
    G = nx.DiGraph()
    G.add_edges_from(pipeline.edges)

    parents  = list(G.predecessors(TARGET_COL))
    children = list(G.successors(TARGET_COL))
    ego_nodes = {TARGET_COL} | set(parents) | set(children)
    ego = G.subgraph(ego_nodes).copy()

    # Layout: parents left, target centre, children right
    pos: dict[str, tuple] = {TARGET_COL: (0.0, 0.0)}
    if parents:
        spacing = 1.6
        total = spacing * (len(parents) - 1)
        for i, p in enumerate(parents):
            pos[p] = (-4.0, total / 2 - i * spacing)
    if children:
        spacing = 0.9
        total = spacing * (len(children) - 1)
        for i, c in enumerate(children):
            pos[c] = (4.0, total / 2 - i * spacing)

    colors = [node_color(n) for n in ego.nodes()]
    labels = {n: (n if n == TARGET_COL else shorten(n)) for n in ego.nodes()}
    edge_colors = [
        EDGE_INTO_TGT if v == TARGET_COL else EDGE_FROM_TGT
        for u, v in ego.edges()
    ]

    fig_h = max(12, max(len(parents), len(children)) * 0.9 + 4)
    fig, ax = plt.subplots(figsize=(16, fig_h))
    nx.draw_networkx(
        ego, pos=pos, ax=ax, labels=labels,
        node_color=colors, node_size=2200,
        edge_color=edge_colors, arrows=True, arrowsize=14,
        font_size=7, font_weight="bold",
        connectionstyle="arc3,rad=0.05",
    )
    # Section labels
    if parents:
        ax.text(-4.0, max(pos[p][1] for p in parents) + 1.5,
                "Parent features\n(predictive of is_vul)",
                ha="center", fontsize=10, color=EDGE_INTO_TGT, fontweight="bold")
    if children:
        ax.text(4.0, max(pos[c][1] for c in children) + 1.5,
                "Child features\n(d-separated by is_vul)",
                ha="center", fontsize=10, color=EDGE_FROM_TGT, fontweight="bold")

    legend_handles = [
        mpatches.Patch(color=EDGE_INTO_TGT, label=f"feature → {TARGET_COL}"),
        mpatches.Patch(color=EDGE_FROM_TGT, label=f"{TARGET_COL} → feature"),
        mpatches.Patch(color=TARGET_COLOR,  label=f"{TARGET_COL} (target)"),
    ]
    ax.legend(handles=legend_handles, loc="lower right", fontsize=9)
    ax.set_title(f"BN1: 1-Hop Neighbourhood of {TARGET_COL}", fontsize=13)
    ax.axis("off")
    plt.tight_layout()
    save_fig(fig, figures_dir / "dag_ego", f"{stem}_dag_ego")
    plt.close(fig)


# ---------------------------------------------------------------------------
# F2: Heatmap of P(is_vul=1 | parent states)
# ---------------------------------------------------------------------------

def figure_heatmap(bn, figures_dir: Path, *, stem: str = "bn1") -> None:
    ve = VariableElimination(bn)
    parents  = list(bn.get_parents(TARGET_COL))       # 3 parents
    vul_idx  = list(bn.get_cpds(TARGET_COL).state_names[TARGET_COL]).index("1")
    short_p  = [shorten(p) for p in parents]

    records = []
    for vals in itertools.product(["0", "1"], repeat=3):
        ev = dict(zip(parents, vals))
        q  = ve.query([TARGET_COL], evidence=ev, show_progress=False)
        records.append({
            parents[0]: int(vals[0]),
            parents[1]: int(vals[1]),
            parents[2]: int(vals[2]),
            "P(is_vul=1)": float(q.values[vul_idx]),
        })
    df = pd.DataFrame(records)

    p0, p1, p2 = parents
    s0, s1, s2 = short_p

    all_probs = df["P(is_vul=1)"].values
    vmin, vmax = all_probs.min() - 0.02, all_probs.max() + 0.02

    fig, axes = plt.subplots(1, 2, figsize=(12, 4), sharey=True)
    for ax, p2_val in zip(axes, [0, 1]):
        sub = df[df[p2] == p2_val].pivot(index=p1, columns=p0, values="P(is_vul=1)")
        sub.index   = [f"{s1}={v}" for v in sub.index]
        sub.columns = [f"{s0}={v}" for v in sub.columns]
        sns.heatmap(
            sub, ax=ax, annot=True, fmt=".3f",
            vmin=vmin, vmax=vmax, center=0.5,
            cmap="RdYlGn_r", linewidths=0.5, linecolor="white",
            cbar=(p2_val == 1), annot_kws={"size": 12},
        )
        ax.set_title(f"{s2} = {p2_val}", fontsize=11)
        ax.set_xlabel(s0, fontsize=10)
        ax.set_ylabel(s1 if p2_val == 0 else "", fontsize=10)

    fig.suptitle(f"P(is_vul=1 | parent states) — BN1", fontsize=13, y=1.04)
    plt.tight_layout()
    save_fig(fig, figures_dir / "heatmap", f"{stem}_heatmap")
    plt.close(fig)


# ---------------------------------------------------------------------------
# F3: MI bar chart
# ---------------------------------------------------------------------------

def figure_mi_bar(mi_series: pd.Series, figures_dir: Path, k: int = 20, *, stem: str = "bn1") -> None:
    top    = mi_series.head(k)
    colors = [ACTION_COLORS.get(action_type(f), DEFAULT_COLOR) for f in top.index]
    names  = [shorten(f) for f in top.index]

    fig, ax = plt.subplots(figsize=(8, 7))
    ax.barh(names[::-1], top.values[::-1], color=colors[::-1],
            edgecolor="white", height=0.72)
    ax.set_xlabel("Mutual Information with is_vul", fontsize=11)
    ax.set_title(f"Top-{k} Features by Mutual Information (BN1)", fontsize=12)
    ax.tick_params(axis="y", labelsize=8)
    ax.spines[["top", "right"]].set_visible(False)

    legend_handles = [
        mpatches.Patch(color=c, label=a) for a, c in ACTION_COLORS.items()
    ]
    ax.legend(handles=legend_handles, fontsize=9, loc="lower right")
    plt.tight_layout()
    save_fig(fig, figures_dir / "mi_bar", f"{stem}_mi_bar")
    plt.close(fig)


# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------

if __name__ == "__main__":
    import argparse

    parser = argparse.ArgumentParser(
        description="Generate BN1 figures and tables.",
        formatter_class=argparse.ArgumentDefaultsHelpFormatter,
    )
    parser.add_argument("--pipeline-file", type=str,
                        default="data/results/bn1_hillclimb_mi50_pipeline.pkl")
    parser.add_argument("--model-file", type=str,
                        default="data/results/bn1_hillclimb_mi50_fitted.pkl")
    parser.add_argument("--output-dir", type=str, default="results")
    parser.add_argument("--stem", type=str, default=None,
                        help="Output filename stem; defaults to pipeline filename minus '_pipeline'")
    args = parser.parse_args()

    project_root = find_project_root()
    out_dir      = project_root / "data" / args.output_dir
    figures_dir  = out_dir / "figures"
    tables_dir   = out_dir / "tables"

    stem = args.stem or Path(args.pipeline_file).stem.removesuffix("_pipeline")
    logger.info(f"Output stem: {stem}")

    logger.info("Loading pipeline and fitted model...")
    pipeline = BNPipeline.load(args.pipeline_file)
    with open(args.model_file, "rb") as f:
        bn = pickle.load(f)

    logger.info("Loading full feature matrix (for T1/T2/F3)...")
    df_raw = pd.read_parquet(
        project_root / "data" / "processed" / "feature_matrix.parquet"
    )

    # ---- Tables ----
    logger.info("T1: dataset summary...")
    save_table(table_dataset_summary(df_raw, TARGET_COL), tables_dir / "dataset", f"{stem}_dataset")

    logger.info("T2/F3: computing MI on all 496 features...")
    mi_series = compute_mi_series(df_raw, TARGET_COL)
    save_table(table_mi_top20(mi_series), tables_dir / "mi_top20", f"{stem}_mi_top20")

    logger.info("T3: structure summary...")
    save_table(table_structure_summary(pipeline, TARGET_COL), tables_dir / "structure", f"{stem}_structure")

    logger.info("T4: lift table (VariableElimination)...")
    save_table(table_lift(bn, TARGET_COL), tables_dir / "lift", f"{stem}_lift")

    # ---- Figures ----
    logger.info("F1a: full DAG...")
    figure_dag_full(pipeline, figures_dir, stem=stem)

    logger.info("F1b: ego graph...")
    figure_dag_ego(pipeline, figures_dir, stem=stem)

    logger.info("F2: heatmap...")
    figure_heatmap(bn, figures_dir, stem=stem)

    logger.info("F3: MI bar chart...")
    figure_mi_bar(mi_series, figures_dir, stem=stem)

    logger.info("All outputs saved.")
