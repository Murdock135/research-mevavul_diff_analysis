"""
Shared Bayesian network utilities: preprocessing, structure learning, and fitting.

BNPipeline holds all intermediate state so that learn_structure() and fit() operate
on identical data — eliminating any risk of row-filtering mismatch between scripts.

Typical usage
-------------
Structure learning (learn_dag_isvul.py):

    pipeline = BNPipeline(df, target_col="is_vul")
    pipeline.preprocess(...).learn_structure(...)
    pipeline.save_edges(out_dir, stem)
    pipeline.save_pipeline(out_dir / f"{stem}_pipeline.pkl")

Fitting (fit_bn1.py):

    pipeline = BNPipeline.load(pipeline_file)
    pipeline.fit(...).print_cpd().save_model(out_dir, stem).save_cpd(out_dir, stem)
"""

import json
import logging
import math
import pickle
import random
import time
from pathlib import Path

import numpy as np
import pandas as pd
from tqdm.auto import tqdm
from pgmpy.base import DAG as PgmDAG
from pgmpy.estimators import BayesianEstimator
from pgmpy.estimators import BIC as BicScore
from pgmpy.models import DiscreteBayesianNetwork
from sklearn.feature_selection import mutual_info_classif

logger = logging.getLogger(__name__)

META_COLS = [
    "is_vul", "id", "func_name", "cve_id", "cwe_ids", "commit_hash",
    "parent_commit_hash", "repo_name", "git_url", "file_path", "file_name",
    "commit_date", "commit_msg", "cvss_vector", "cvss_base_score",
    "cvss_base_severity", "cvss_is_v3",
]


# ---------------------------------------------------------------------------
# Standalone preprocessing helpers
# ---------------------------------------------------------------------------

def threshold_features(df: pd.DataFrame, threshold: float = 0.05) -> pd.DataFrame:
    """Remove features non-zero in fewer than `threshold` fraction of samples."""
    if threshold > 1:
        logger.warning("Threshold >1 — interpreting as percentage.")
        threshold /= 100
    mask = (df != 0).sum(axis=0) / len(df) >= threshold
    kept = int(mask.sum())
    logger.info(f"threshold_features: kept {kept} / {len(mask)} features (threshold={threshold})")
    return df.loc[:, mask]


def threshold_samples(df: pd.DataFrame, threshold: float = 0.0) -> pd.DataFrame:
    """Remove samples with fewer than `threshold` fraction of non-zero features."""
    if threshold > 1:
        logger.warning("Threshold >1 — interpreting as percentage.")
        threshold /= 100
    mask = (df != 0).sum(axis=1) / df.shape[1] >= threshold
    removed = int((~mask).sum())
    if removed:
        logger.info(f"threshold_samples: removed {removed} rows (threshold={threshold})")
    return df.loc[mask, :]


def select_top_mi_features(X: pd.DataFrame, y: pd.Series, k: int) -> list[str]:
    """Return names of top-k features ranked by mutual information with y."""
    mi = mutual_info_classif(X.values, y.values, discrete_features=True, random_state=0)
    mi_series = pd.Series(mi, index=X.columns).sort_values(ascending=False)
    selected = mi_series.head(k).index.tolist()
    logger.info(f"MI selection: kept {len(selected)} / {len(X.columns)} features (top {k})")
    logger.debug(f"Top MI features:\n{mi_series.head(min(k, 15)).to_string()}")
    return selected


# ---------------------------------------------------------------------------
# HCS helpers
# ---------------------------------------------------------------------------

_HCS_CONST = 2 * math.sqrt(2) + math.sqrt(3)  # concentration constant from Dann et al.


def _random_dag(nodes: list[str], n_edges: int, rng: random.Random) -> PgmDAG:
    """Random acyclic graph via topological-order trick.

    Shuffles nodes to fix a random total order, then only adds edges from
    lower-index to higher-index positions — guaranteeing acyclicity without
    any cycle checks.
    """
    dag = PgmDAG()
    dag.add_nodes_from(nodes)
    ordered = nodes[:]
    rng.shuffle(ordered)
    candidates = [
        (ordered[i], ordered[j])
        for i in range(len(ordered))
        for j in range(i + 1, len(ordered))
    ]
    rng.shuffle(candidates)
    for u, v in candidates[:n_edges]:
        dag.add_edge(u, v)
    return dag


# ---------------------------------------------------------------------------
# BNPipeline
# ---------------------------------------------------------------------------

class BNPipeline:
    """
    Stateful pipeline for Bayesian network structure learning and fitting.

    State progression:
      __init__  →  preprocess()  →  learn_structure()  →  fit()
                                     load_edges()  ↗

    After preprocess(), the raw DataFrame is released (set to None) to keep
    the pickled pipeline lightweight.
    """

    def __init__(self, df: pd.DataFrame, target_col: str) -> None:
        self._df: pd.DataFrame | None = df
        self.target_col = target_col
        self.model_df: pd.DataFrame | None = None
        self.edges: list[tuple[str, str]] | None = None
        self.bn: DiscreteBayesianNetwork | None = None
        # HCS restart metadata (populated by learn_structure when method='hillclimb')
        self.hcs_history: list[dict] | None = None      # per-restart: {restart, score, f1, cn, hcs_c}
        self.edge_inclusion: dict[tuple, int] | None = None  # edge -> count across restarts
        self.hcs_n_restarts: int | None = None          # total restarts performed

    # ------------------------------------------------------------------
    # Preprocessing
    # ------------------------------------------------------------------

    def preprocess(
        self,
        *,
        feature_threshold: float = 0.0001,
        sample_threshold: float = 0.0,
        mi_threshold: int | None = None,
    ) -> "BNPipeline":
        """Build model_df: filter samples/features, optionally apply MI selection."""
        assert self._df is not None, "preprocess() has already been called; _df was released"
        feature_cols = [c for c in self._df.columns if c not in META_COLS]

        model_df = self._df[feature_cols + [self.target_col]].copy()
        model_df = threshold_samples(model_df, threshold=sample_threshold)
        feat_df = threshold_features(
            model_df.drop(columns=[self.target_col]), threshold=feature_threshold
        )
        model_df = feat_df.join(model_df[self.target_col]).astype(int)
        logger.info(f"After sparsity filter: {model_df.shape} ({len(feat_df.columns)} features)")

        if mi_threshold is not None:
            selected = select_top_mi_features(
                model_df.drop(columns=[self.target_col]),
                model_df[self.target_col],
                k=mi_threshold,
            )
            model_df = model_df[selected + [self.target_col]]
            logger.info(f"After MI selection: {model_df.shape}")

        # Cast to str so pgmpy infers columns as categorical ('C') not numerical ('N').
        # MI selection and threshold filters require numeric input, so this must be last.
        model_df = model_df.astype(str)
        self.model_df = model_df
        self._df = None  # release raw data; no longer needed
        return self

    # ------------------------------------------------------------------
    # Structure learning
    # ------------------------------------------------------------------

    def learn_structure(
        self,
        *,
        method: str = "hillclimb",
        scoring_method: str | None = None,
        significance_level: float = 0.01,
        ci_test: str = "chi_square",
        tabu_length: int = 100,
        max_indegree: int | None = None,
        max_iter: int = 1_000_000,
        # HCS random-restart parameters (hillclimb only)
        hcs_delta: float = 0.05,
        hcs_c: float = 0.05,
        hcs_max_restarts: int = 100,
        hcs_n_edges: int | None = None,
        show_progress: bool = True,
    ) -> "BNPipeline":
        """Run structure learning on model_df; sets self.edges.

        For hillclimb, uses the HCS stopping criterion (Dann, Dick & Wong):
        runs random restarts until the Good-Turing missing-mass bound cn drops
        below hcs_c with confidence 1-hcs_delta, or hcs_max_restarts is hit.

        Parameters
        ----------
        method            : 'hillclimb' or 'mmhc'
        scoring_method    : scoring function; defaults to 'bdeu' (mmhc) or 'bic-d' (hillclimb)
        significance_level: p-value threshold for MMHC independence tests
        ci_test           : CI test for MMHC skeleton phase
        tabu_length       : tabu list length (both methods)
        max_indegree      : maximum parents per node; None = unrestricted
        max_iter          : maximum HC iterations per restart
        hcs_delta         : confidence parameter δ; bound holds with prob ≥ 1-δ
        hcs_c             : missing-mass target; stop when cn < hcs_c
        hcs_max_restarts  : hard cap on restarts (safety valve)
        hcs_n_edges       : edges in each random starting DAG; default = n_nodes // 4
        show_progress     : show tqdm bar per restart (True); fall back to per-restart INFO log (False)
        """
        assert self.model_df is not None, "Call preprocess() before learn_structure()"

        kwargs: dict = {}
        if max_indegree is not None:
            kwargs["max_indegree"] = max_indegree

        if method == "hillclimb":
            from pgmpy.estimators import HillClimbSearch

            score = scoring_method or "bic-d"
            nodes = list(self.model_df.columns)
            n_edges = hcs_n_edges if hcs_n_edges is not None else max(1, len(nodes) // 4)
            scorer = BicScore(self.model_df)

            def dag_score(dag) -> float:
                return sum(
                    scorer.local_score(n, list(dag.predecessors(n))) for n in dag.nodes()
                )

            edge_key_counts: dict[frozenset, int] = {}
            edge_inclusion_counts: dict[tuple, int] = {}
            history: list[dict] = []
            best_dag, best_score = None, -np.inf

            logger.info(
                f"HCS HillClimbSearch: score={score}, tabu={tabu_length}, "
                f"max_iter={max_iter}, hcs_c={hcs_c}, hcs_delta={hcs_delta}, "
                f"hcs_max={hcs_max_restarts}, n_edges_per_restart={n_edges}"
            )
            t0 = time.time()
            converged = False
            with tqdm(
                range(hcs_max_restarts),
                desc="HCS",
                unit="restart",
                disable=not show_progress,
            ) as pbar:
                for restart in pbar:
                    n = restart + 1
                    start_dag = (
                        None if restart == 0
                        else _random_dag(nodes, n_edges, random.Random(restart))
                    )
                    dag = HillClimbSearch(self.model_df).estimate(
                        scoring_method=score,
                        start_dag=start_dag,
                        tabu_length=tabu_length,
                        max_iter=max_iter,
                        show_progress=False,
                        **kwargs,
                    )
                    key = frozenset(dag.edges())
                    s = dag_score(dag)
                    edge_key_counts[key] = edge_key_counts.get(key, 0) + 1
                    for edge in dag.edges():
                        edge_inclusion_counts[edge] = edge_inclusion_counts.get(edge, 0) + 1

                    if s > best_score:
                        best_score, best_dag = s, dag

                    f1 = sum(1 for cnt in edge_key_counts.values() if cnt == 1)
                    cn = f1 / n + _HCS_CONST * math.sqrt(math.log(3 / hcs_delta) / n)
                    history.append({
                        "restart": n, "score": s, "f1": f1, "cn": cn, "hcs_c": hcs_c,
                        "edges": [list(e) for e in dag.edges()],
                    })
                    if show_progress:
                        pbar.set_postfix(
                            score=f"{s:.0f}", cn=f"{cn:.3f}",
                            best=f"{best_score:.0f}", f1=f1, refresh=False,
                        )
                    else:
                        logger.info(
                            f"  restart {n:3d}: score={s:.1f}, edges={len(key)}, "
                            f"f1={f1}, cn={cn:.4f} (target<{hcs_c}), best={best_score:.1f}"
                        )
                    if cn < hcs_c:
                        logger.info(f"HCS converged after {n} restarts ({time.time()-t0:.1f}s)")
                        converged = True
                        break

            if not converged:
                logger.warning(
                    f"HCS reached hard cap of {hcs_max_restarts} restarts without converging "
                    f"(final cn={cn:.4f}, target={hcs_c})"
                )

            self.hcs_history = history
            self.edge_inclusion = edge_inclusion_counts
            self.hcs_n_restarts = len(history)
            learned_dag = best_dag

        elif method == "mmhc":
            from pgmpy.estimators import MmhcEstimator
            score = scoring_method or "bdeu"
            logger.info(
                f"Starting MMHC (score={score}, ci_test={ci_test}, "
                f"significance_level={significance_level})..."
            )
            t0 = time.time()
            learned_dag = MmhcEstimator(self.model_df).estimate(
                scoring_method=score,
                significance_level=significance_level,
                tabu_length=tabu_length,
                **kwargs,
            )
        else:
            raise ValueError(f"Unknown method: {method!r}. Choose 'hillclimb' or 'mmhc'.")

        elapsed = time.time() - t0
        logger.info(f"Structure learning complete in {elapsed:.1f}s")

        self.edges = list(learned_dag.edges())
        target_edges = [(u, v) for u, v in self.edges if self.target_col in (u, v)]
        logger.info(f"Total edges: {len(self.edges)}, on {self.target_col}: {len(target_edges)}")
        logger.debug(f"Edges on {self.target_col}: {target_edges}")
        return self

    def load_edges(self, edges_file: Path | str) -> "BNPipeline":
        """Load edges from a CSV instead of running structure learning."""
        edges_df = pd.read_csv(edges_file)
        self.edges = list(edges_df.itertuples(index=False, name=None))
        logger.info(f"Loaded {len(self.edges)} edges from {edges_file}")
        return self

    # ------------------------------------------------------------------
    # Parameter fitting
    # ------------------------------------------------------------------

    def fit(self, *, equivalent_sample_size: float = 5.0) -> "BNPipeline":
        """Fit CPDs on model_df using BayesianEstimator (BDeu prior)."""
        assert self.model_df is not None, "Call preprocess() before fit()"
        assert self.edges is not None, "Call learn_structure() or load_edges() before fit()"

        bn = DiscreteBayesianNetwork(self.edges)
        logger.info(f"Fitting BN (BDeu, ESS={equivalent_sample_size})...")
        bn.fit(
            self.model_df,
            estimator=BayesianEstimator,
            prior_type="BDeu",
            equivalent_sample_size=equivalent_sample_size,
        )
        logger.info("Fitting complete.")
        self.bn = bn
        return self

    # ------------------------------------------------------------------
    # Persistence
    # ------------------------------------------------------------------

    def save_edges(self, out_dir: Path, stem: str) -> "BNPipeline":
        assert self.edges is not None, "No edges to save — run learn_structure() first"
        out_dir.mkdir(parents=True, exist_ok=True)
        pd.DataFrame(self.edges, columns=["source", "target"]).to_csv(
            out_dir / f"{stem}_edges.csv", index=False
        )
        with open(out_dir / f"{stem}_edges.json", "w") as f:
            json.dump([{"source": u, "target": v} for u, v in self.edges], f, indent=2)
        logger.info(f"Saved edges → {out_dir / stem}_edges.{{csv,json}}")
        return self

    def save_pipeline(self, path: Path) -> "BNPipeline":
        path.parent.mkdir(parents=True, exist_ok=True)
        with open(path, "wb") as f:
            pickle.dump(self, f)
        logger.info(f"Saved pipeline → {path}")
        return self

    @classmethod
    def load(cls, path: Path | str) -> "BNPipeline":
        with open(path, "rb") as f:
            pipeline = pickle.load(f)
        logger.info(f"Loaded pipeline from {path}")
        return pipeline

    def save_model(self, out_dir: Path, stem: str) -> "BNPipeline":
        assert self.bn is not None, "No model to save — run fit() first"
        out_dir.mkdir(parents=True, exist_ok=True)
        model_path = out_dir / f"{stem}_fitted.pkl"
        with open(model_path, "wb") as f:
            pickle.dump(self.bn, f)
        logger.info(f"Saved model → {model_path}")
        return self

    # ------------------------------------------------------------------
    # Inspection
    # ------------------------------------------------------------------

    def print_cpd(self, node: str | None = None) -> "BNPipeline":
        assert self.bn is not None, "Run fit() before print_cpd()"
        node = node or self.target_col
        if node not in self.bn.nodes():
            logger.warning(f"{node!r} not found in BN nodes.")
            return self
        print(f"\nCPD for {node}:\n")
        print(self.bn.get_cpds(node))
        return self

    def save_cpd(self, out_dir: Path, stem: str, node: str | None = None) -> "BNPipeline":
        assert self.bn is not None, "Run fit() before save_cpd()"
        node = node or self.target_col
        if node not in self.bn.nodes():
            logger.warning(f"{node!r} not found in BN nodes.")
            return self
        cpd_path = out_dir / f"{stem}_cpd_{node}.txt"
        cpd_path.write_text(str(self.bn.get_cpds(node)))
        logger.info(f"Saved CPD → {cpd_path}")
        return self
