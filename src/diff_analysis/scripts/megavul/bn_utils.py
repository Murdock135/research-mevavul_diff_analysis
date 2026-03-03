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
import pickle
import time
from pathlib import Path

import pandas as pd
from pgmpy.estimators import BayesianEstimator
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
        tabu_length: int = 10,
        max_indegree: int | None = None,
        max_iter: int = 1000,
    ) -> "BNPipeline":
        """Run structure learning on model_df; sets self.edges.

        Parameters
        ----------
        method           : 'hillclimb' or 'mmhc'; defaults to 'hillclimb'
        scoring_method   : scoring function; defaults to 'bdeu' (mmhc) or 'bic-d' (hillclimb)
        significance_level: p-value threshold for MMHC independence tests
        ci_test          : conditional independence test for MMHC skeleton phase
                           ('chi_square', 'g_sq', 'log_likelihood')
        tabu_length      : tabu list length (both methods)
        max_indegree     : maximum parents per node; None = unrestricted
        max_iter         : maximum hill-climbing iterations (hillclimb only)
        """
        assert self.model_df is not None, "Call preprocess() before learn_structure()"

        kwargs: dict = {}
        if max_indegree is not None:
            kwargs["max_indegree"] = max_indegree

        if method == "hillclimb":
            from pgmpy.estimators import HillClimbSearch
            score = scoring_method or "bic-d"
            logger.info(f"Starting HillClimbSearch (score={score}, max_iter={max_iter}, tabu_length={tabu_length})...")
            t0 = time.time()
            learned_dag = HillClimbSearch(self.model_df).estimate(
                scoring_method=score,
                tabu_length=tabu_length,
                max_iter=max_iter,
                **kwargs,
            )
        elif method == "mmhc":
            from pgmpy.estimators import MmhcEstimator
            score = scoring_method or "bdeu"
            logger.info(f"Starting MMHC (score={score}, ci_test={ci_test}, significance_level={significance_level})...")
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
        logger.info(f"Total edges: {len(self.edges)}")
        logger.info(f"Edges on {self.target_col} ({len(target_edges)}): {target_edges}")
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
