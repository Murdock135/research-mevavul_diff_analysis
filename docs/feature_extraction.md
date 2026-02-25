# AST Diff Feature Extraction

This document describes the complete feature extraction pipeline: from raw C source code pairs to the structured numeric feature vectors used for downstream analysis (Bayesian network, classifiers, etc.).

---

## 1. Overview

Each sample in the dataset is a **vulnerable/patched function pair** — two versions of the same C function, one containing a known vulnerability (CVE-tagged) and one representing the developer's fix. The goal of feature extraction is to characterize *how* the patch changes the code at the AST level, producing a tabular feature vector per pair.

The extraction pipeline has two stages:

```
(vulnerable.c, patched.c)
        │
        ▼
   GumTree textdiff
        │
        ▼
   GumTree XML diff   ──►  GumTreeDiffParser  ──►  GumTreeDiff
                                                         │
                                                         ▼
                                               GumTreeFeatureExtractor
                                                         │
                                                         ▼
                                             feature dict (tabular row)
```

**Stage 1 — AST diffing (GumTree):** GumTree parses both files into abstract syntax trees and computes the minimum-edit-script between them using the GumTree algorithm (Falleri et al., 2014). The output is an XML document listing *actions* (edit operations) and *matches* (node correspondences between the two ASTs).

**Stage 2 — Feature extraction:** A Python extractor reads the parsed diff and computes six groups of features. Features are all scalars (integer, float, or boolean) and are collected into a single flat dictionary per pair.

---

## 2. Running Example

The following pair is used as a concrete illustration throughout this document. It is a minimal buffer-overflow fix representative of CWE-120/CWE-787 patches in the dataset.

**Vulnerable (`vulnerable.c`):**
```c
void write_data(char *dst, char *src) {
    strcpy(dst, src);
    return;
}
```

**Patched (`patched.c`):**
```c
void write_data(char *dst, char *src) {
    if (dst == NULL) return;
    strncpy(dst, src, 63);
    return;
}
```

The patch makes two structural changes: (1) it inserts a NULL-guard `if`-block before the copy, and (2) it replaces `strcpy` with the bounded `strncpy` and adds a size argument `63`.

### 2.1 Byte offsets

Byte offsets (0-indexed, exclusive end) for nodes referenced later. All offsets are exact for the code above.

| Node | File | Span |
|------|------|------|
| `function_definition` | both | `[0, 75)` / `[0, 109)` |
| `identifier: write_data` | both | `[5, 15)` |
| `parameter_list` | both | `[15, 38)` |
| `compound_statement` | both | `[38, 75)` / `[38, 109)` |
| `call_expression` (strcpy call) | vuln | `[44, 60)` |
| `identifier: strcpy` | vuln | `[44, 50)` |
| `argument_list` | vuln | `[50, 60)` |
| `identifier: dst` (arg) | vuln | `[51, 54)` |
| `identifier: src` (arg) | vuln | `[56, 59)` |
| `return_statement` (end) | vuln | `[66, 73)` |
| `if_statement` | patch | `[44, 68)` |
| `identifier: strncpy` | patch | `[73, 80)` |
| `argument_list` | patch | `[80, 94)` |
| `identifier: dst` (arg) | patch | `[81, 84)` |
| `identifier: src` (arg) | patch | `[86, 89)` |
| `number_literal: 63` | patch | `[91, 93)` |
| `return_statement` (end) | patch | `[100, 107)` |

### 2.2 GumTree XML output

GumTree produces the following XML (lightly formatted; omitting parameter sub-nodes for brevity):

```xml
<matches>
  <match src="function_definition [0, 75]"     dest="function_definition [0, 109]"/>
  <match src="identifier: write_data [5, 15]"  dest="identifier: write_data [5, 15]"/>
  <match src="parameter_list [15, 38]"         dest="parameter_list [15, 38]"/>
  <match src="compound_statement [38, 75]"     dest="compound_statement [38, 109]"/>
  <match src="call_expression [44, 60]"        dest="call_expression [73, 94]"/>
  <match src="identifier: strcpy [44, 50]"     dest="identifier: strncpy [73, 80]"/>
  <match src="argument_list [50, 60]"          dest="argument_list [80, 94]"/>
  <match src="identifier: dst [51, 54]"        dest="identifier: dst [81, 84]"/>
  <match src="identifier: src [56, 59]"        dest="identifier: src [86, 89]"/>
  <match src="return_statement [66, 73]"       dest="return_statement [100, 107]"/>
</matches>
<actions>
  <insert-tree tree="if_statement [44, 68]"
               parent="compound_statement [38, 109]" at="0"/>
  <update-node tree="identifier: strcpy [44, 50]"
               label="strncpy"/>
  <insert-node tree="number_literal: 63 [91, 93]"
               parent="argument_list [80, 94]" at="2"/>
</actions>
```

Three things to note before reading further:

1. The `insert-tree` action span `[44, 68)` refers to a position in the **patched** file (where the `if_statement` lives). The `update-node` action span `[44, 50)` refers to the **vulnerable** file (where `strcpy` lives). These are in different coordinate systems even though the numbers happen to overlap.
2. The `if_statement` match entry is absent: there is no corresponding node in the vulnerable file, so it cannot be matched — only inserted.
3. The `return_statement` *inside* the if-block is not listed as a separate action; it is implicitly part of the `insert-tree` subtree.

### 2.3 The parsed GumTreeDiff

After parsing, the diff object holds:

```
GumTreeDiff(
  actions = [
    GumTreeAction(kind="insert-tree",
                  tree=NodeRef(type="if_statement",   label=None, start=44, end=68),
                  parent=NodeRef(type="compound_statement", ..., start=38, end=109),
                  at=0),
    GumTreeAction(kind="update-node",
                  tree=NodeRef(type="identifier", label="strcpy", start=44, end=50),
                  label="strncpy"),
    GumTreeAction(kind="insert-node",
                  tree=NodeRef(type="number_literal", label="63",  start=91, end=93),
                  parent=NodeRef(type="argument_list", ..., start=80, end=94),
                  at=2),
  ],
  matches = [
    GumTreeMatch(src=NodeRef("function_definition", None, 0,  75),
                 dst=NodeRef("function_definition", None, 0, 109)),
    GumTreeMatch(src=NodeRef("identifier", "strcpy", 44, 50),
                 dst=NodeRef("identifier", "strncpy", 73, 80)),
    ... (8 more matches)
  ],
  src_to_dst = {
    (0,  75): NodeRef("function_definition", None, 0,  109),
    (44, 50): NodeRef("identifier", "strncpy", 73, 80),
    (50, 60): NodeRef("argument_list", None, 80, 94),
    (51, 54): NodeRef("identifier", "dst", 81, 84),
    (56, 59): NodeRef("identifier", "src", 86, 89),
    (66, 73): NodeRef("return_statement", None, 100, 107),
    ...
  },
  dst_to_src = { ... }  # inverse map
)
```

---

## 3. The GumTree Diff Representation

### 3.1 Node references

Every AST node referenced in the diff is represented as a `NodeRef` — simply the 4-tuple produced by parsing a GumTree node string. There is no separate node ID; the `(start, end)` span serves as the sole identity for matching purposes.

| Field   | Type    | Description |
|---------|---------|-------------|
| `type`  | `str`   | GumTree node type, e.g. `if_statement`, `call_expression`, `identifier` |
| `label` | `str?`  | The token text when the node is a leaf, e.g. `malloc`, `NULL`, `42`; `None` for structural nodes |
| `start` | `int`   | Byte offset (inclusive) of the node in the source file |
| `end`   | `int`   | Byte offset (exclusive) of the node in the source file |

GumTree node type strings follow tree-sitter grammar names for C. Common examples: `function_definition`, `compound_statement`, `if_statement`, `for_statement`, `call_expression`, `binary_expression`, `identifier`, `number_literal`, `string_literal`.

### 3.2 Actions

An **action** is a single edit operation on the source AST. GumTree produces six action kinds:

| Kind          | Meaning |
|---------------|---------|
| `insert-node` | A single node is inserted into the destination AST (children are handled separately) |
| `insert-tree` | A subtree (node + all descendants) is inserted |
| `delete-node` | A single node is deleted from the source AST |
| `delete-tree` | A subtree (node + all descendants) is deleted |
| `update-node` | A leaf node's label is changed (e.g. variable renamed, constant updated) |
| `move-tree`   | A subtree is relocated (different parent or position) |

Each `GumTreeAction` has:
- `kind` — one of the six strings above
- `tree` — the `NodeRef` of the affected node (the root of the subtree for `*-tree` actions)
- `parent` — the `NodeRef` of the destination parent (for `insert-*` and `move-tree`)
- `at` — child position index in the parent (for `insert-*` and `move-tree`)
- `label` — for `update-node`, the *new* label value

**Key interpretation:** `action.tree` spans refer to the **source (vulnerable) file** for delete/update actions, and to the **destination (patched) file** for insert actions. GumTree does not mark this distinction explicitly in the XML — the convention follows from the action semantics.

**Running example:** Our diff has three actions, one of each of the three kinds that appear:
- `insert-tree` — the new `if_statement` subtree. Its span `[44, 68)` is in the patched file.
- `update-node` — the `strcpy → strncpy` rename. Its span `[44, 50)` is in the vulnerable file.
- `insert-node` — the new `63` argument. Its span `[91, 93)` is in the patched file.

### 3.3 Matches

A **match** links a node in the source AST to its corresponding node in the destination AST. Matched nodes are considered *unchanged* in structure (though their labels may differ if an `update-node` applies to a leaf). The parser builds two span-indexed lookup tables:

```
src_to_dst : (start, end) → NodeRef   # given a source span, find the destination node
dst_to_src : (start, end) → NodeRef   # given a destination span, find the source node
```

These maps are used during feature extraction to pair changed regions across the two code versions.

**Running example:** There are 10 matches. Notably, `identifier: strcpy [44, 50)` is matched to `identifier: strncpy [73, 80)` — the call-site structure is preserved; only the leaf label changes. Consequently, `src_to_dst[(44, 50)] = NodeRef("identifier", "strncpy", 73, 80)`.

### 3.4 Parsing notes

GumTree's XML output has two sibling top-level elements (`<matches>` and `<actions>`), which is not well-formed XML. The parser wraps them in a synthetic `<root>` element before passing to `ElementTree`. Node reference strings like `"if_statement: foo [10, 42]"` are parsed with a regex that separates the type, optional label, and the `[start, end]` span.

---

## 4. Node Type Normalization

Before counting node types, raw GumTree type strings are mapped to a canonical form:

```python
def canonical_node_type(t: str) -> str:
    if not t or t == "<empty>":
        return "unknown"
    if t.startswith("comment"):
        return "comment"           # collapse all comment variants
    if t.startswith("preproc") or t.startswith("#"):
        return "preproc"           # collapse preprocessor directives
    if re.match(r"^[\W_]+$", t):
        return "token"             # punctuation/operator tokens
    return t                       # pass through unchanged
```

This prevents feature explosion from GumTree's fine-grained comment and preprocessor subtypes, and avoids creating per-punctuation features for structural tokens like `{`, `;`, etc.

**Running example:** The three action nodes have types `if_statement`, `identifier`, and `number_literal`. None trigger the normalization rules, so all pass through unchanged.

---

## 5. Feature Groups

The extractor produces features in six groups. All groups are always computed except Groups 5 and 6, which require the raw source code strings to be passed in (not currently done in the batch extraction script — only the XML diffs are used).

### 5.1 Global Diff Counts (`_global_counts`)

**What it measures:** The total volume and type distribution of AST edit operations.

**Features produced:**

| Feature name         | Type    | Description |
|----------------------|---------|-------------|
| `n_actions`          | `int`   | Total number of actions in the edit script |
| `n_matches`          | `int`   | Total number of matched node pairs |
| `n_delete_node`      | `int`   | Count of `delete-node` actions |
| `n_delete_tree`      | `int`   | Count of `delete-tree` actions |
| `n_update_node`      | `int`   | Count of `update-node` actions |
| `n_insert_node`      | `int`   | Count of `insert-node` actions |
| `n_insert_tree`      | `int`   | Count of `insert-tree` actions |
| `n_move_tree`        | `int`   | Count of `move-tree` actions |
| `act_<kind>`         | `int`   | Raw per-kind count (one feature per observed kind; same values as above) |
| `ratio_delete_tree`  | `float` | `n_delete_tree / n_actions` |
| `ratio_update_node`  | `float` | `n_update_node / n_actions` |
| `ratio_move_tree`    | `float` | `n_move_tree / n_actions` |

**Pseudocode:**

```
kind_counts = Counter(action.kind for action in diff.actions)

n_actions = len(diff.actions)
n_matches = len(diff.matches)

for each (kind, count) in kind_counts:
    act_{kind} = count

ratio_delete_tree = n_delete_tree / max(1, n_actions)
ratio_update_node = n_update_node / max(1, n_actions)
ratio_move_tree   = n_move_tree   / max(1, n_actions)
```

**Interpretation:** Large `n_actions` indicates a structurally invasive patch. High `ratio_delete_tree` suggests the patch removes whole code subtrees (e.g., removing an unsafe branch). High `ratio_update_node` suggests the patch is mostly name/constant changes rather than structural rewrites.

**Running example:**

```
kind_counts = {"insert-tree": 1, "update-node": 1, "insert-node": 1}

n_actions          = 3
n_matches          = 10
n_insert_tree      = 1    →  act_insert-tree = 1
n_update_node      = 1    →  act_update-node = 1
n_insert_node      = 1    →  act_insert-node = 1
n_delete_node      = 0
n_delete_tree      = 0
n_move_tree        = 0

ratio_delete_tree  = 0 / 3 = 0.0
ratio_update_node  = 1 / 3 ≈ 0.333
ratio_move_tree    = 0 / 3 = 0.0
```

The low `n_actions` (3) and zero delete counts reflect a purely additive patch — new code was inserted and one identifier was renamed, with nothing removed.

---

### 5.2 Touched Node Type Features (`_touched_node_type_features`)

**What it measures:** Which AST node types appear in the edit script, and how often.

**Features produced:**

| Feature name              | Type   | Description |
|---------------------------|--------|-------------|
| `n_unique_touched_types`  | `int`  | Number of distinct canonical node types across all actions |
| `touches_sensitive_ast`   | `bool` | Any touched type is in the *sensitive types* set (see below) |
| `node_<type>`             | `int`  | Count of actions touching nodes of canonical type `<type>` (top-30 by frequency) |

**Sensitive AST types** (security-relevant expression classes):
```
call_expression, binary_expression, unary_expression,
assignment_expression, pointer_expression, field_expression,
array_subscript_expression, conditional_expression
```

**Pseudocode:**

```
touched_types = [canonical_node_type(action.tree.type)
                 for action in diff.actions
                 if action.tree is not None]

type_counts = Counter(touched_types)

n_unique_touched_types = len(type_counts)
touches_sensitive_ast  = any(t in SENSITIVE_TYPES for t in type_counts)

for (node_type, count) in type_counts.most_common(top_k=30):
    node_{node_type} = count
```

**Interpretation:** The `node_*` features are count-weighted one-hot encodings of node types. For example, `node_if_statement = 3` means three actions involved `if_statement` nodes. Only the top-30 types by frequency across the diff are retained per sample to control dimensionality.

**Running example:**

The three actions reference nodes of types: `if_statement`, `identifier`, `number_literal`.

```
touched_types = ["if_statement", "identifier", "number_literal"]
type_counts   = {if_statement: 1, identifier: 1, number_literal: 1}

n_unique_touched_types = 3
touches_sensitive_ast  = False
    # None of {if_statement, identifier, number_literal}
    # are in SENSITIVE_TYPES

node_if_statement   = 1
node_identifier     = 1
node_number_literal = 1
```

Note that the `binary_expression` (`dst == NULL`) inside the inserted `if_statement` is **not** listed as a touched type. The `insert-tree` action records only the subtree root (`if_statement`); all descendants are implicitly inserted and do not generate separate action entries.

---

### 5.3 Control Flow Features (`_control_flow_features`)

**What it measures:** Whether the patch touches control-flow AST nodes, and whether it specifically inserts or deletes conditional branches.

**Control flow types:**
```
if_statement, for_statement, while_statement, do_statement,
switch_statement, return_statement, break_statement,
continue_statement, goto_statement
```

**Features produced:**

| Feature name          | Type   | Description |
|-----------------------|--------|-------------|
| `touches_control_flow`| `bool` | Any action touches a control-flow node |
| `touches_if`          | `bool` | Any action touches an `if_statement` |
| `touches_return`      | `bool` | Any action touches a `return_statement` |
| `touches_loop`        | `bool` | Any action touches `for_statement`, `while_statement`, or `do_statement` |
| `deleted_if`          | `bool` | At least one `delete-*` action on an `if_statement` |
| `inserted_if`         | `bool` | At least one `insert-*` action on an `if_statement` |

**Pseudocode:**

```
touched_types = {action.tree.type
                 for action in diff.actions
                 if action.tree is not None}

touches_control_flow = touched_types ∩ CONTROL_FLOW_TYPES ≠ ∅
touches_if     = "if_statement" ∈ touched_types
touches_return = "return_statement" ∈ touched_types
touches_loop   = {"for_statement","while_statement","do_statement"} ∩ touched_types ≠ ∅

deleted_if = ∃ action : action.kind.startswith("delete")
                     ∧ action.tree.type == "if_statement"

inserted_if = ∃ action : action.kind.startswith("insert")
                      ∧ action.tree.type == "if_statement"
```

**Interpretation:** `inserted_if` being true is consistent with a patch that adds a guard check. `deleted_if` may indicate removal of an unsafe or incorrect conditional. These are among the most semantically interpretable features for vulnerability analysis.

**Running example:**

```
touched_types = {"if_statement", "identifier", "number_literal"}

touches_control_flow = True   # if_statement ∈ CONTROL_FLOW_TYPES
touches_if           = True
touches_return       = False  # return_statement is NOT in touched_types —
                              # the early `return;` is inside the inserted
                              # if_statement subtree, but the action root is
                              # if_statement, not return_statement
touches_loop         = False

deleted_if  = False  # no delete-* action with tree.type == "if_statement"
inserted_if = True   # insert-tree action with tree.type == "if_statement"
```

The `touches_return = False` result illustrates a subtlety: the early `return;` added by the patch exists in the AST, but because it is a *descendant* of the inserted `if_statement`, GumTree captures it implicitly within the `insert-tree` action rather than as a separate action. Only the subtree root appears in `touched_types`.

---

### 5.4 Update Label Features (`_update_label_features`)

**What it measures:** The set of identifier names and values that were changed in-place (as opposed to being deleted or inserted wholesale).

**Features produced:**

| Feature name              | Type   | Description |
|---------------------------|--------|-------------|
| `n_updated_labels`        | `int`  | Number of `update-node` actions with a non-null new label |
| `updated_labels_joined`   | `str`  | Pipe-separated list of the new label values (up to 50; for inspection) |
| `has_safety_token_update` | `bool` | Any updated label is in the safety token set |

**Safety tokens:**
```
snprintf, strncpy, memcpy, memmove, calloc, realloc, NULL
```

**Pseudocode:**

```
updated_labels = [action.label
                  for action in diff.actions
                  if action.kind == "update-node" AND action.label is not None]

n_updated_labels        = len(updated_labels)
updated_labels_joined   = "|".join(updated_labels[:50])
has_safety_token_update = updated_labels ∩ SAFETY_TOKENS ≠ ∅
```

**Interpretation:** `update-node` captures renaming and constant replacement — e.g., replacing `sprintf` with `snprintf`, or changing a literal bound. `has_safety_token_update` fires when a patched function introduces a safer API call by updating an existing call-site node rather than deleting and reinserting it.

**Running example:**

There is one `update-node` action: `tree = identifier: strcpy [44, 50)`, `label = "strncpy"`. The `label` field holds the *new* value.

```
updated_labels = ["strncpy"]

n_updated_labels        = 1
updated_labels_joined   = "strncpy"
has_safety_token_update = True   # "strncpy" ∈ SAFETY_TOKENS
```

This demonstrates how GumTree and the extractor together detect a common vulnerability-fix pattern: a call-site rename from an unsafe C string function to its bounded equivalent, without full structural reconstruction of the call expression.

---

### 5.5 Security / Vulnerability-Specific Features (`_security_features`)

> **Note:** These features require the raw source code strings (`vulnerable_code`, `patched_code`) to be passed to the extractor. In the current batch extraction script, only the XML diffs are used, so these features are **not computed** and are absent from `gumtree_features.csv`. They are documented here for completeness and future use.

**What it measures:** Memory management operations touched by the patch, and coarse heuristics for added/removed null checks and boundary comparisons.

**Features produced:**

| Feature name              | Type   | Description |
|---------------------------|--------|-------------|
| `touches_memory_ops`      | `bool` | Any action's label is in `{malloc, calloc, realloc, free}` |
| `deleted_memory_op`       | `bool` | A `delete-*` action whose node label is a memory op |
| `inserted_memory_op`      | `bool` | An `insert-*` action whose node label is a memory op |
| `added_null_check`        | `bool` | `NULL` appears in changed regions of patched code but not vulnerable code |
| `removed_null_check`      | `bool` | `NULL` appears in changed regions of vulnerable code but not patched code |
| `inserted_boundary_check` | `bool` | Comparison operators (`<`, `<=`, `>`, `>=`) appear in changed patched regions but not vulnerable |
| `deleted_boundary_check`  | `bool` | Comparison operators appear in changed vulnerable regions but not patched |

**Snippet pairing for text heuristics:**

To compute `added_null_check`, `removed_null_check`, `inserted_boundary_check`, and `deleted_boundary_check`, the extractor pairs text snippets from both files around each changed AST node span. For each action with a valid `tree` span in the vulnerable file:

```
for action in diff.actions where action.tree is not None:
    s, e = action.tree.start, action.tree.end
    window = 60  # characters of context around each span

    # Vulnerable snippet (always from source file)
    v_snippet = vulnerable_code[max(0, s-window) : min(len(vuln), e+window)]

    # Patched snippet — use match map to locate corresponding region
    dst = src_to_dst.get((s, e))
    if dst exists and bounds are valid:
        p_snippet = patched_code[max(0, dst.start-window) : min(len(patch), dst.end+window)]
    else:
        # Fallback: same byte offsets in patched file
        p_snippet = patched_code[max(0, s-window) : min(len(patch), e+window)]

v_text = concat(all v_snippets)
p_text = concat(all p_snippets)

added_null_check        = "NULL" ∈ p_text ∧ "NULL" ∉ v_text
removed_null_check      = "NULL" ∈ v_text ∧ "NULL" ∉ p_text
inserted_boundary_check = any(op ∈ p_text for op in ["<","<=",">",">="]) ∧
                          NOT any(op ∈ v_text for op in ["<","<=",">",">="])
deleted_boundary_check  = any(op ∈ v_text ...) ∧ NOT any(op ∈ p_text ...)
```

**Known limitations:**
- The comparison operator heuristic (`<`, `<=`, etc.) is broad and fires on arithmetic expressions, not just bounds checks. A true bounds-check detector would require operator context (left-hand operand is an index, right-hand is a length).
- The `added_null_check` heuristic uses string search on `NULL`, which can fire on comments or string literals.
- The fallback span mapping (same byte offsets) is approximate and may not correspond to semantically related code.

**Running example:**

Starting from the three actions and the `src_to_dst` map:

```
Action 1 — insert-tree if_statement [44, 68) (patched-file span):
  s, e = 44, 68
  v_snippet = vulnerable[max(0,44-60) : min(75, 68+60)]
            = vulnerable[0:75]           # entire vulnerable function
  src_to_dst.get((44, 68)) → None        # if_statement has no match
  fallback: p_snippet = patched[0:109]   # entire patched function

Action 2 — update-node identifier:strcpy [44, 50) (vulnerable-file span):
  s, e = 44, 50
  v_snippet = vulnerable[0:75]           # entire vulnerable function
  src_to_dst.get((44, 50)) → NodeRef("identifier", "strncpy", 73, 80)
  p_snippet = patched[max(0,73-60) : min(109, 80+60)]
            = patched[13:109]

Action 3 — insert-node number_literal:63 [91, 93) (patched-file span):
  s, e = 91, 93
  v_snippet = vulnerable[31:75]          # valid bounds (31 ≤ 75)
  src_to_dst.get((91, 93)) → None
  fallback: p_snippet = patched[31:109]

v_text = concat of all v_snippets:
  contains "strcpy(dst, src)" and "return;" — no NULL, no </>
p_text = concat of all p_snippets:
  contains "if (dst == NULL) return;" and "strncpy(dst, src, 63);"

touches_memory_ops  = False  # no action label is in {malloc, calloc, realloc, free}
deleted_memory_op   = False
inserted_memory_op  = False

added_null_check    = True   # "NULL" ∈ p_text ∧ "NULL" ∉ v_text
removed_null_check  = False

# Boundary operators ["<", "<=", ">", ">="] — note "==" is NOT in this list
v_has_cmp = False
p_has_cmp = False            # "==" in p_text, but "==" is not a boundary op
inserted_boundary_check = False
deleted_boundary_check  = False
```

The correctly distinguishes the added null-equality check (`added_null_check = True`) from a bounds-comparison check (`inserted_boundary_check = False`) — the patch uses `==`, not `<`/`<=`/`>`/`>=`.

---

### 5.6 Span Text Features (`_span_text_features`)

> **Note:** Also requires raw source code strings; absent from the current batch extraction output.

**What it measures:** Lexical properties of the source code regions that were modified.

**Features produced:**

| Feature name                | Type    | Description |
|-----------------------------|---------|-------------|
| `avg_changed_snippet_len`   | `float` | Mean character length of changed AST node spans in the vulnerable file |
| `max_changed_snippet_len`   | `int`   | Maximum character length across changed spans |
| `snippet_has_NULL`          | `bool`  | The string `NULL` appears in any changed span |
| `snippet_has_memcpy`        | `bool`  | The string `memcpy` appears in any changed span |
| `snippet_has_strcpy`        | `bool`  | The string `strcpy` appears in any changed span |
| `snippet_has_bounds_op`     | `bool`  | Any of `<`, `<=`, `>`, `>=` appears in any changed span |

**Pseudocode:**

```
snippets = [vulnerable_code[action.tree.start : action.tree.end]
            for action in diff.actions
            if action.tree is not None
            AND 0 ≤ action.tree.start < action.tree.end ≤ len(vulnerable_code)]

if snippets:
    avg_changed_snippet_len = mean(len(s) for s in snippets)
    max_changed_snippet_len = max(len(s) for s in snippets)
    joined = concat(snippets)
    snippet_has_NULL      = "NULL" ∈ joined
    snippet_has_memcpy    = "memcpy" ∈ joined
    snippet_has_strcpy    = "strcpy" ∈ joined
    snippet_has_bounds_op = any(op ∈ joined for op in ["<","<=",">",">="])
```

**Running example:**

The check `0 ≤ s < e ≤ len(vulnerable_code)` is applied to each action span against the vulnerable file length (75):

```
Action 1 — insert-tree [44, 68): 68 ≤ 75 ✓
  vulnerable[44:68] = "strcpy(dst, src);\n    re"   (24 chars)
  ⚠ This is bytes from the VULNERABLE file at the same offsets as the
    patched-file if_statement — a spurious read caused by the span-side
    ambiguity (see §6).

Action 2 — update-node [44, 50): 50 ≤ 75 ✓
  vulnerable[44:50] = "strcpy"   (6 chars)

Action 3 — insert-node [91, 93): 93 > 75 ✗ — skipped

snippets = ["strcpy(dst, src);\n    re", "strcpy"]

avg_changed_snippet_len = (24 + 6) / 2 = 15.0
max_changed_snippet_len = 24
joined = "strcpy(dst, src);\n    rextrcpy"

snippet_has_NULL      = False
snippet_has_memcpy    = False
snippet_has_strcpy    = True    # "strcpy" appears in joined
snippet_has_bounds_op = False
```

This illustrates a concrete instance of the span-side ambiguity: the insert-tree action span `[44, 68)` belongs to the patched file, but the extractor reads it from the vulnerable file and obtains `"strcpy(dst, src);\n    re"` — bytes that have no relation to the inserted `if_statement`. Only the update-node span `[44, 50)` correctly corresponds to a vulnerable-file region.

---

### 5.7 Hunk Grouping and Group-Level Features

**What it measures:** Whether the patch consists of one localized change or multiple scattered edits, and whether control-flow modifications are concentrated or spread across the function.

#### 5.7.1 The Hunk Grouping Algorithm

Actions are clustered into *change groups* (analogous to diff hunks) by merging actions whose AST spans overlap or are within `span_merge_gap` characters of each other. The algorithm is a single-pass sweep:

```
# Filter: only actions with an attached tree node
anchored = [(action.tree.span, action)
            for action in diff.actions if action.tree is not None]

# Sort by span start position, then end
anchored.sort(key=lambda x: (x[0][0], x[0][1]))

groups = []
cur_group = []
cur_start, cur_end = anchored[0].span

for (span, action) in anchored:
    s, e = span
    if s ≤ cur_end + span_merge_gap:   # overlapping or close enough
        cur_group.append(action)
        cur_end = max(cur_end, e)      # expand group bounds
    else:
        groups.append(cur_group)       # finalize current hunk
        cur_group = [action]
        cur_start, cur_end = s, e

groups.append(cur_group)  # flush last group

# Build ChangeGroup objects
for (i, group_actions) in enumerate(groups):
    g_start = min(a.tree.start for a in group_actions)
    g_end   = max(a.tree.end   for a in group_actions)
    ChangeGroup(group_id=i, src_span=(g_start, g_end), actions=group_actions)
```

The default `span_merge_gap = 0` requires strict span overlap. Setting it to a positive value (e.g., 10–50) merges nearby but non-overlapping edits, which can be useful when GumTree's edit script decomposes a logically single change into adjacent node operations.

#### 5.7.2 Group Summary Features (`_group_summary_features`)

| Feature name              | Type    | Description |
|---------------------------|---------|-------------|
| `n_groups`                | `int`   | Number of distinct change groups |
| `max_group_actions`       | `int`   | Size (action count) of the largest group |
| `avg_group_actions`       | `float` | Mean group size |
| `pct_groups_with_delete`  | `float` | Fraction of groups that contain at least one `delete-*` action |

#### 5.7.3 Group Control Flow Features (`_group_control_flow_features`)

| Feature name              | Type   | Description |
|---------------------------|--------|-------------|
| `groups_with_control_flow`| `int`  | Count of groups that contain at least one action on a control-flow node |

**Pseudocode:**

```
n_groups     = len(groups)
group_sizes  = [len(g.actions) for g in groups]

max_group_actions      = max(group_sizes)
avg_group_actions      = mean(group_sizes)
pct_groups_with_delete = |{g : ∃ a ∈ g.actions, a.kind.startswith("delete")}| / n_groups

groups_with_control_flow = |{g : ∃ a ∈ g.actions,
                                  a.tree ≠ None ∧ a.tree.type ∈ CONTROL_FLOW_TYPES}|
```

**Interpretation:** `n_groups = 1` means all changes are localized to a single contiguous region. `n_groups > 1` means the patch touches multiple disjoint locations in the function. `groups_with_control_flow / n_groups` gives the proportion of change regions that affect program logic, vs. those that are purely data/expression changes.

**Running example:**

Sorting the three actions by `(start, end)`:

```
1. update-node  identifier:strcpy    span [44, 50)
2. insert-tree  if_statement         span [44, 68)
3. insert-node  number_literal:63    span [91, 93)
```

Sweep (span_merge_gap = 0):

```
Initialize from action 1: cur_group=[update-node], cur_start=44, cur_end=50

Action 2 — span [44, 68):
  44 ≤ 50 + 0? Yes  →  same group
  cur_group = [update-node, insert-tree], cur_end = max(50, 68) = 68

Action 3 — span [91, 93):
  91 ≤ 68 + 0? No   →  new group
  finalize Group 0: [update-node, insert-tree]
  cur_group = [insert-node], cur_start=91, cur_end=93

Flush: Group 1 = [insert-node]
```

Final groups:

| Group | src_span | Actions |
|-------|----------|---------|
| 0 | `[44, 68)` | update-node (strcpy), insert-tree (if_statement) |
| 1 | `[91, 93)` | insert-node (63) |

```
n_groups              = 2
group_sizes           = [2, 1]
max_group_actions     = 2
avg_group_actions     = 1.5
pct_groups_with_delete = 0 / 2 = 0.0   # no group has a delete action

groups_with_control_flow = 1
  # Group 0: insert-tree root is if_statement ∈ CONTROL_FLOW_TYPES ✓
  # Group 1: insert-node root is number_literal ∉ CONTROL_FLOW_TYPES ✗
```

The two groups correspond to the two logically distinct parts of the patch: Group 0 captures the combined effect of inserting the guard block and renaming the function (their spans overlap because both happen at byte offset 44 in their respective files), and Group 1 captures the addition of the size bound argument.

---

## 6. Complete Feature Reference

The table below lists every feature produced by the extractor in its default configuration (group features enabled, code text not provided, label vocab disabled). Features marked † require raw code text and are absent from the current batch extraction output. The final column shows the value for the running example.

| Group         | Feature name               | Type    | Always? | Running example |
|---------------|----------------------------|---------|---------|-----------------|
| Global counts | `n_actions`                | int     | ✓ | 3 |
|               | `n_matches`                | int     | ✓ | 10 |
|               | `n_delete_node`            | int     | ✓ | 0 |
|               | `n_delete_tree`            | int     | ✓ | 0 |
|               | `n_update_node`            | int     | ✓ | 1 |
|               | `n_insert_node`            | int     | ✓ | 1 |
|               | `n_insert_tree`            | int     | ✓ | 1 |
|               | `n_move_tree`              | int     | ✓ | 0 |
|               | `act_insert-tree`          | int     | ✓ | 1 |
|               | `act_update-node`          | int     | ✓ | 1 |
|               | `act_insert-node`          | int     | ✓ | 1 |
|               | `ratio_delete_tree`        | float   | ✓ | 0.0 |
|               | `ratio_update_node`        | float   | ✓ | 0.333 |
|               | `ratio_move_tree`          | float   | ✓ | 0.0 |
| Node types    | `n_unique_touched_types`   | int     | ✓ | 3 |
|               | `touches_sensitive_ast`    | bool    | ✓ | False |
|               | `node_if_statement`        | int     | ✓ | 1 |
|               | `node_identifier`          | int     | ✓ | 1 |
|               | `node_number_literal`      | int     | ✓ | 1 |
| Control flow  | `touches_control_flow`     | bool    | ✓ | True |
|               | `touches_if`               | bool    | ✓ | True |
|               | `touches_return`           | bool    | ✓ | False |
|               | `touches_loop`             | bool    | ✓ | False |
|               | `deleted_if`               | bool    | ✓ | False |
|               | `inserted_if`              | bool    | ✓ | True |
| Update labels | `n_updated_labels`         | int     | ✓ | 1 |
|               | `updated_labels_joined`    | str     | ✓ | "strncpy" |
|               | `has_safety_token_update`  | bool    | ✓ | True |
| Security      | `touches_memory_ops`       | bool    | † | False |
|               | `deleted_memory_op`        | bool    | † | False |
|               | `inserted_memory_op`       | bool    | † | False |
|               | `added_null_check`         | bool    | † | True |
|               | `removed_null_check`       | bool    | † | False |
|               | `inserted_boundary_check`  | bool    | † | False |
|               | `deleted_boundary_check`   | bool    | † | False |
| Span text     | `avg_changed_snippet_len`  | float   | † | 15.0 |
|               | `max_changed_snippet_len`  | int     | † | 24 |
|               | `snippet_has_NULL`         | bool    | † | False |
|               | `snippet_has_memcpy`       | bool    | † | False |
|               | `snippet_has_strcpy`       | bool    | † | True |
|               | `snippet_has_bounds_op`    | bool    | † | False |
| Hunk grouping | `n_groups`                 | int     | ✓ | 2 |
|               | `max_group_actions`        | int     | ✓ | 2 |
|               | `avg_group_actions`        | float   | ✓ | 1.5 |
|               | `pct_groups_with_delete`   | float   | ✓ | 0.0 |
|               | `groups_with_control_flow` | int     | ✓ | 1 |

† Requires `vulnerable_code` and `patched_code` to be passed to `extractor.extract()`.

---

## 7. Design Choices and Limitations

**Action spans as proxies for change location.** GumTree action spans come from the *source* (vulnerable) file for delete/update actions and from the *destination* (patched) file for insert actions. The extractor does not distinguish these sides — all spans are treated as source-side when computing snippet features. This is a simplification that can introduce imprecision in the text-based heuristics (Groups 5 and 6). The running example demonstrates this concretely: the `insert-tree` span `[44, 68)` reads an unrelated region of the vulnerable file.

**Top-K node type truncation.** The `node_*` features only capture the 30 most-frequent node types per diff. Rare node types (e.g., `goto_statement`) are silently dropped. This prevents feature explosion across projects with different coding styles, but means that uncommon-but-informative AST patterns are not captured.

**Boolean security heuristics are coarse.** Features like `deleted_boundary_check` fire on any removal of a comparison operator from a changed region, regardless of whether that comparison was actually a bounds check. Similarly, `added_null_check` fires on any appearance of the string `NULL` in a patched region. These should be interpreted as proxies rather than precise semantic detectors.

**`insert-tree` hides descendant node types.** When GumTree inserts an entire subtree, only the root appears as the action node. Descendants (e.g., the `binary_expression` condition inside an inserted `if_statement`) are not reflected in `touched_types` or `node_*` features. This means `touches_sensitive_ast` can be False even when a semantically sensitive expression class is added by the patch.

**The `updated_labels_joined` feature is not used in modeling.** It is a diagnostic string retained for manual inspection (e.g., to verify what identifiers changed). It is dropped or ignored before passing features to the Bayesian network.

**GumTree is not type-aware.** GumTree operates on the syntactic AST; it does not know about types, pointer semantics, or dataflow. Features that appear to be "memory safety" related (e.g., `touches_memory_ops`, `inserted_memory_op`) are label-matching heuristics, not semantic analyses.

**Span-merge gap is zero by default.** Adjacent but non-overlapping edits (e.g., two insertions separated by an unchanged token) are placed in separate groups. Increasing `span_merge_gap` to a small value (10–30 characters) would merge such cases but risks conflating logically distinct changes.
