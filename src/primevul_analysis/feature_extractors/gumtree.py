from __future__ import annotations

from dataclasses import dataclass
from typing import Dict, List, Optional, Tuple, Union
from collections import Counter
import re

from primevul_analysis.types import GumTreeDiff, GumTreeAction, ChangeGroup


CONTROL_FLOW_TYPES = {
    "if_statement", "for_statement", "while_statement", "do_statement",
    "switch_statement", "return_statement", "break_statement", "continue_statement",
    "goto_statement",
}

SENSITIVE_TYPES = {
    # Useful in vuln-related patches
    "call_expression", "binary_expression", "unary_expression", "assignment_expression",
    "pointer_expression", "field_expression", "array_subscript_expression",
    "conditional_expression",
}

MEMORY_OPS = {"malloc", "calloc", "realloc", "free"}

_PUNCT_RE = re.compile(r"^[\W_]+$")  # only non-alphanumerics/underscore

def canonical_node_type(t: str) -> str:
    t = (t or "").strip()

    if not t or t == "<empty>":
        return "unknown"

    # GumTree sometimes produces "comment: <text...>"
    if t.startswith("comment"):
        return "comment"

    # preprocessor families
    if t.startswith("preproc") or t.startswith("#"):
        return "preproc"

    # punctuation / token-like nodes
    if _PUNCT_RE.match(t):
        return "token"

    return t



class GumTreeFeatureExtractor:
    """
    Extracts feature vectors from GumTreeDiff.

    Supports:
      - global diff features (counts, touched node-types)
      - grouped features (hunks) via span overlap clustering
      - optional lexical features if you provide code text
    
    Example output:
    {
        'n_actions': 12,
        'n_delete_tree': 3,
        'n_update_node': 2,
        'ratio_delete_tree': 0.25,
        'touches_control_flow': True,
        'deleted_if': True,
        'inserted_if': False,
        'deleted_boundary_check': True,
        'touches_memory_ops': True,
        'has_safety_token_update': False,
        'n_groups': 3,
        'avg_group_actions': 4.0,
        'snippet_has_NULL': True,
        ...
    }
    """

    def __init__(
        self,
        *,
        top_k_node_types: int = 30,
        top_k_labels: int = 30,
        group_by_spans: bool = True,
        span_merge_gap: int = 0,
    ) -> None:
        """
        top_k_node_types:
            How many distinct node_type counts to include as features (node_<type> = count).
            Keeping top-K prevents feature explosion across projects.

        top_k_labels:
            Similar idea for labels (identifier names, etc.). This can explode dimensionality.
            Default should usually be low or disabled unless you use hashing.

        group_by_spans:
            Whether to compute "hunk" features by clustering actions using span overlap.

        span_merge_gap:
            If > 0, treat spans within this many characters as overlapping.
            Helps merge actions that are near each other but not strictly overlapping.
        """
        self.top_k_node_types = top_k_node_types
        self.top_k_labels = top_k_labels
        self.group_by_spans = group_by_spans
        self.span_merge_gap = span_merge_gap

    # -------------------------
    # Public API
    # -------------------------

    def extract(
        self,
        diff: GumTreeDiff,
        *,
        vulnerable_code: Optional[str] = None,
        patched_code: Optional[str] = None,
        include_group_features: bool = True,
        include_label_vocab: bool = False,
    ) -> Dict[str, Union[int, float, bool, str]]:
        """
        Extract features from a GumTreeDiff.
        
        Input:
          diff: GumTreeDiff (actions + matches + maps)
          vulnerable_code/patched_code: optional code text for lexical heuristics
          include_group_features: include hunk stats if grouping enabled
          include_label_vocab: include label bag-of-words (can be high dimensional)

        Output:
          Flat dict {feature_name: feature_value}
          Values are scalars: int/float/bool/str (tabular-friendly)
        """
        feats: Dict[str, Union[int, float, bool, str]] = {}

        feats.update(self._global_counts(diff))
        feats.update(self._touched_node_type_features(diff))
        feats.update(self._control_flow_features(diff))
        feats.update(self._update_label_features(diff))

        if vulnerable_code is not None and patched_code is not None:
            feats.update(self._span_text_features(diff, vulnerable_code, patched_code))
            feats.update(self._security_features(
                diff,
                vulnerable_code=vulnerable_code,
                patched_code=patched_code,
            ))

        if include_group_features and self.group_by_spans:
            groups = self.group_actions(diff.actions)
            feats.update(self._group_summary_features(groups))
            feats.update(self._group_control_flow_features(groups))

        if include_label_vocab:
            feats.update(self._top_label_vocab_features(diff))

        return feats

    def group_actions(self, actions: List[GumTreeAction]) -> List[ChangeGroup]:
        """
        Groups actions into span-based 'hunks' using the action.tree span.
        If action.tree is None, it is placed into a special bucket (ignored by default).
        """
        spans_actions: List[Tuple[Tuple[int, int], GumTreeAction]] = [] # List (span, action) = List ( (start, end), action )
        for a in actions:
            if a.tree is None:
                continue
            spans_actions.append((a.tree.span, a))

        if not spans_actions:
            return []

        # sort by span start
        spans_actions.sort(key=lambda x: (x[0][0], x[0][1]))

        groups: List[List[GumTreeAction]] = []
        cur_actions: List[GumTreeAction] = []
        cur_start, cur_end = spans_actions[0][0] # initialize current group bounds using first action span

        def overlaps_or_close(s1: Tuple[int, int], s2: Tuple[int, int]) -> bool:
            """
            Returns True if spans s1 and s2 overlap OR are within span_merge_gap.
            """
            (a, b), (c, d) = s1, s2
            return not (d < a - self.span_merge_gap or c > b + self.span_merge_gap)

        for (sp, act) in spans_actions:
            if not cur_actions:
                # Start first group
                cur_actions = [act]
                cur_start, cur_end = sp
                continue

            if overlaps_or_close((cur_start, cur_end), sp):
                # Same hunk: expand current bounds to include new span
                cur_actions.append(act)
                cur_start = min(cur_start, sp[0])
                cur_end = max(cur_end, sp[1])
            else:
                # New hunk: finalize current and start new
                groups.append(cur_actions)
                cur_actions = [act]
                cur_start, cur_end = sp

        # Flush last group
        if cur_actions:
            groups.append(cur_actions)

        # Convert list-of-actions to ChangeGroup with overall span
        out: List[ChangeGroup] = []
        for i, acts in enumerate(groups):
            g_start = min(a.tree.start for a in acts if a.tree)  # type: ignore
            g_end = max(a.tree.end for a in acts if a.tree)      # type: ignore
            out.append(ChangeGroup(group_id=i, src_span=(g_start, g_end), actions=tuple(acts)))
        return out

    # -------------------------
    # Feature blocks
    # -------------------------

    def _global_counts(self, diff: GumTreeDiff) -> Dict[str, Union[int, float]]:
        """
        Basic counts and ratios of action kinds.
        """
        feats: Dict[str, Union[int, float]] = {}
        feats["n_matches"] = len(diff.matches)
        feats["n_actions"] = len(diff.actions)

        kind_counts = Counter(a.kind for a in diff.actions) 
        for k, v in kind_counts.items():
            feats[f"act_{k}"] = v

        # Useful rollups
        feats["n_delete_node"] = kind_counts.get("delete-node", 0)
        feats["n_delete_tree"] = kind_counts.get("delete-tree", 0)
        feats["n_update_node"] = kind_counts.get("update-node", 0)
        feats["n_insert_node"] = kind_counts.get("insert-node", 0)
        feats["n_insert_tree"] = kind_counts.get("insert-tree", 0)
        feats["n_move_tree"] = kind_counts.get("move-tree", 0)

        # Ratios (safe division)
        n = max(1, feats["n_actions"])
        feats["ratio_delete_tree"] = feats["n_delete_tree"] / n
        feats["ratio_update_node"] = feats["n_update_node"] / n
        feats["ratio_move_tree"] = feats["n_move_tree"] / n

        return feats

    def _touched_node_type_features(self, diff: GumTreeDiff) -> Dict[str, Union[int, bool]]:
        """
        Features based on the types of AST nodes touched by any action.

        For each action with a tree node, we count its node type.
        This gives features like:
          node_if_statement = 2
          node_call_expression = 5
          ...
        """
        feats: Dict[str, Union[int, bool]] = {}

        touched = [canonical_node_type(a.tree.type) for a in diff.actions if a.tree is not None]
        c = Counter(touched)

        feats["n_unique_touched_types"] = len(c)
        feats["touches_sensitive_ast"] = any(t in SENSITIVE_TYPES for t in c.keys())

        # top-K one-hot-ish counts
        for t, v in c.most_common(self.top_k_node_types):
            feats[f"node_{t}"] = v

        return feats

    def _control_flow_features(self, diff: GumTreeDiff) -> Dict[str, bool]:
        """
        Features indicating whether control-flow related AST nodes were touched.
        """
        feats: Dict[str, bool] = {}

        touched_types = {a.tree.type for a in diff.actions if a.tree is not None}
        feats["touches_control_flow"] = any(t in CONTROL_FLOW_TYPES for t in touched_types)

        # more specific
        feats["touches_if"] = "if_statement" in touched_types
        feats["touches_return"] = "return_statement" in touched_types
        feats["touches_loop"] = any(t in {"for_statement", "while_statement", "do_statement"} for t in touched_types)

        # delete/insert of control-flow specifically
        feats["deleted_if"] = any(a.kind.startswith("delete") and a.tree and a.tree.type == "if_statement" for a in diff.actions)
        feats["inserted_if"] = any(a.kind.startswith("insert") and a.tree and a.tree.type == "if_statement" for a in diff.actions)

        return feats

    def _security_features(
        self,
        diff: GumTreeDiff,
        *,
        vulnerable_code: Optional[str] = None,
        patched_code: Optional[str] = None,
    ) -> Dict[str, bool]:
        """
        Extra heuristics aimed at vulnerability-related semantics.

        NOTE (important):
          Some of these are currently broad proxies.
          E.g. "deleted_boundary_check" currently fires on deletion of any if/binary_expression.
          For a true "bounds check" motif, you usually need code text or operator tokens.
        """
        feats: Dict[str, bool] = {}

        # Collect any labels we can see
        tree_labels = [a.tree.label for a in diff.actions if a.tree and a.tree.label]
        action_labels = [a.label for a in diff.actions if a.label]
        labels = set(tree_labels + action_labels)

        feats["touches_memory_ops"] = any(lbl in MEMORY_OPS for lbl in labels)
        feats["deleted_memory_op"] = any(
            a.kind.startswith("delete") and (
                (a.tree and a.tree.label in MEMORY_OPS) or (a.label in MEMORY_OPS)
            )
            for a in diff.actions
        )
        feats["inserted_memory_op"] = any(
            a.kind.startswith("insert") and (
                (a.tree and a.tree.label in MEMORY_OPS) or (a.label in MEMORY_OPS)
            )
            for a in diff.actions
        )

        # Only attempt boundary-check motifs if we have code text
        if vulnerable_code is None or patched_code is None:
            feats["deleted_boundary_check"] = False
            feats["inserted_boundary_check"] = False
            feats["added_null_check"] = False
            feats["removed_null_check"] = False
            return feats

        # Simple heuristic: look at vulnerable snippet around changed spans vs patched snippet
        # (This is intentionally conservative.)
        v_snips, p_snips = self._paired_snippets(diff, vulnerable_code, patched_code)

        v_join = " ".join(v_snips)
        p_join = " ".join(p_snips)

        # Null-check delta (rough but effective)
        feats["added_null_check"] = ("NULL" in p_join) and ("NULL" not in v_join)
        feats["removed_null_check"] = ("NULL" in v_join) and ("NULL" not in p_join)

        # Bounds-check-ish delta: comparisons introduced/removed
        bound_ops = ["<", "<=", ">", ">="]
        v_has_cmp = any(op in v_join for op in bound_ops)
        p_has_cmp = any(op in p_join for op in bound_ops)

        feats["inserted_boundary_check"] = p_has_cmp and not v_has_cmp
        feats["deleted_boundary_check"] = v_has_cmp and not p_has_cmp

        return feats

    def _paired_snippets(
        self, diff: GumTreeDiff, vuln: str, patch: str, window: int = 60
    ) -> Tuple[List[str], List[str]]:
        """
        Conservative snippet pairing:
        - Use action.tree spans as anchors in vulnerable code
        - Use match map (src_to_dst) to locate corresponding spans in patched code when possible
        - Fallback: same span indices if no match
        """
        v_snips: List[str] = []
        p_snips: List[str] = []

        for a in diff.actions:
            if a.tree is None:
                continue
            s, e = a.tree.start, a.tree.end
            if not (0 <= s < e <= len(vuln)):
                continue

            # Expand a small window for lexical context
            vs = max(0, s - window)
            ve = min(len(vuln), e + window)
            v_snips.append(vuln[vs:ve])

            # Try to map to dest span using matches
            dst = diff.src_to_dst.get((s, e))
            if dst and 0 <= dst.start < dst.end <= len(patch):
                ps = max(0, dst.start - window)
                pe = min(len(patch), dst.end + window)
                p_snips.append(patch[ps:pe])
            else:
                # fallback: same indices
                ps = max(0, s - window)
                pe = min(len(patch), e + window)
                if ps < pe <= len(patch):
                    p_snips.append(patch[ps:pe])

        return v_snips, p_snips

    def _update_label_features(self, diff: GumTreeDiff) -> Dict[str, Union[int, str, bool]]:
        """
        Features based on labels changed in update-node actions.
        E.g., changing a function name, variable name, constant value, etc.
        """
        feats: Dict[str, Union[int, str, bool]] = {}
        updated = [a.label for a in diff.actions if a.kind == "update-node" and a.label]
        feats["n_updated_labels"] = len(updated)

        # Keep compact; you can hash or vocab later
        # For now, store a joined string for analysis/debug (optional)
        feats["updated_labels_joined"] = "|".join(updated[:50])

        # Common C safety tokens (starter; extend later)
        safety_tokens = {"snprintf", "strncpy", "memcpy", "memmove", "calloc", "realloc", "NULL"}
        feats["has_safety_token_update"] = any(lbl in safety_tokens for lbl in updated)

        return feats

    def _top_label_vocab_features(self, diff: GumTreeDiff) -> Dict[str, int]:
        """
        Optional: include top-K labels touched by actions.
        This can blow up dimensionality; only enable if you really want it.
        """
        feats: Dict[str, int] = {}
        labels = [a.tree.label for a in diff.actions if a.tree is not None and a.tree.label]
        c = Counter(labels)
        for lbl, v in c.most_common(self.top_k_labels):
            # sanitize key a bit
            safe = lbl.replace(" ", "_").replace("|", "_")[:80]
            feats[f"label_{safe}"] = v
        return feats

    def _span_text_features(self, diff: GumTreeDiff, vuln: str, patch: str) -> Dict[str, Union[float, int, bool]]:
        """
        If you pass in code strings, you can extract a little lexical signal using spans:
          - avg length of changed snippets
          - whether added/removed 'NULL', comparison operators, etc.
        NOTE: spans are per-file; if GumTree spans are for src and dest separately,
        then only use action.tree spans with the corresponding side. Since actions don't
        explicitly say src vs dest, we treat them as src spans for now.
        """
        feats: Dict[str, Union[float, int, bool]] = {}
        snippets: List[str] = []

        for a in diff.actions:
            if a.tree is None:
                continue
            s, e = a.tree.start, a.tree.end
            if 0 <= s < e <= len(vuln):
                snippets.append(vuln[s:e])

        if snippets:
            lens = [len(x) for x in snippets]
            feats["avg_changed_snippet_len"] = sum(lens) / len(lens)
            feats["max_changed_snippet_len"] = max(lens)

            joined = " ".join(snippets)
            feats["snippet_has_NULL"] = "NULL" in joined
            feats["snippet_has_memcpy"] = "memcpy" in joined
            feats["snippet_has_strcpy"] = "strcpy" in joined
            feats["snippet_has_bounds_op"] = any(op in joined for op in ["<", "<=", ">", ">="])
        else:
            feats["avg_changed_snippet_len"] = 0.0
            feats["max_changed_snippet_len"] = 0

        return feats

    def _group_summary_features(self, groups: List[ChangeGroup]) -> Dict[str, Union[int, float]]:
        """
        Summary statistics over change groups/hunks.
        """
        feats: Dict[str, Union[int, float]] = {}
        feats["n_groups"] = len(groups)

        if not groups:
            feats["max_group_actions"] = 0
            feats["avg_group_actions"] = 0.0
            return feats

        sizes = [len(g.actions) for g in groups]
        feats["max_group_actions"] = max(sizes)
        feats["avg_group_actions"] = sum(sizes) / len(sizes)

        # proportion of groups containing deletes
        feats["pct_groups_with_delete"] = sum(
            any(a.kind.startswith("delete") for a in g.actions) for g in groups
        ) / len(groups)

        return feats

    def _group_control_flow_features(self, groups: List[ChangeGroup]) -> Dict[str, int]:
        """
        Count how many groups/hunks touch control-flow nodes.

        Example: group contains insertion of an if-statement => hunk touches control flow.
        """
        feats: Dict[str, int] = {}
        if not groups:
            feats["groups_with_control_flow"] = 0
            return feats

        feats["groups_with_control_flow"] = sum(
            any(a.tree and a.tree.type in CONTROL_FLOW_TYPES for a in g.actions) for g in groups
        )
        return feats