# Extracted Features

All features are extracted by `GumTreeFeatureExtractor` in [src/primevul_analysis/feature_extractors/gumtree.py](../src/primevul_analysis/feature_extractors/gumtree.py) from a parsed `GumTreeDiff`.

The pipeline in `scripts/extract_features.py` runs with `include_group_features=True` and `include_label_vocab=False`, and without code strings — so **groups 1–5 are always active**; groups 6–8 require opt-in or code text.

---

## 1. Global Counts (`_global_counts`)

Basic counts and ratios of edit action kinds.

| Feature | Type | Description |
|---|---|---|
| `n_matches` | int | Number of matched node pairs between source and destination ASTs |
| `n_actions` | int | Total number of edit actions |
| `act_<kind>` | int | Raw count per action kind (e.g. `act_delete-node`, `act_insert-tree`) |
| `n_delete_node` | int | Count of `delete-node` actions |
| `n_delete_tree` | int | Count of `delete-tree` actions |
| `n_update_node` | int | Count of `update-node` actions |
| `n_insert_node` | int | Count of `insert-node` actions |
| `n_insert_tree` | int | Count of `insert-tree` actions |
| `n_move_tree` | int | Count of `move-tree` actions |
| `ratio_delete_tree` | float | `n_delete_tree / n_actions` |
| `ratio_update_node` | float | `n_update_node / n_actions` |
| `ratio_move_tree` | float | `n_move_tree / n_actions` |

---

## 2. Touched Node Types (`_touched_node_type_features`)

Features based on which AST node types are involved in any action.

| Feature | Type | Description |
|---|---|---|
| `n_unique_touched_types` | int | Number of distinct AST node types touched |
| `touches_sensitive_ast` | bool | Any action touches a "sensitive" type: `call_expression`, `binary_expression`, `unary_expression`, `assignment_expression`, `pointer_expression`, `field_expression`, `array_subscript_expression`, `conditional_expression` |
| `node_<type>` | int | Count of actions touching each of the top-30 most common AST node types (e.g. `node_if_statement`, `node_identifier`) |

---

## 3. Control Flow (`_control_flow_features`)

Flags for whether control-flow AST nodes were touched, inserted, or deleted.

| Feature | Type | Description |
|---|---|---|
| `touches_control_flow` | bool | Any action touches any control-flow node (`if`, `for`, `while`, `do`, `switch`, `return`, `break`, `continue`, `goto`) |
| `touches_if` | bool | An `if_statement` was touched |
| `touches_return` | bool | A `return_statement` was touched |
| `touches_loop` | bool | A `for_statement`, `while_statement`, or `do_statement` was touched |
| `deleted_if` | bool | An `if_statement` was deleted |
| `inserted_if` | bool | An `if_statement` was inserted |

---

## 4. Update Label Features (`_update_label_features`)

Features about labels (identifier names, constant values) changed via `update-node` actions.

| Feature | Type | Description |
|---|---|---|
| `n_updated_labels` | int | Number of `update-node` actions that carry a label |
| `updated_labels_joined` | str | Pipe-joined list of updated labels (first 50), for debugging/inspection |
| `has_safety_token_update` | bool | Any updated label is in `{snprintf, strncpy, memcpy, memmove, calloc, realloc, NULL}` |

---

## 5. Group / Hunk Features (`_group_summary_features` + `_group_control_flow_features`)

Actions are first clustered into overlapping-span "hunks" using `group_actions()` before these are computed. Enabled by default (`include_group_features=True`).

| Feature | Type | Description |
|---|---|---|
| `n_groups` | int | Number of distinct change hunks |
| `max_group_actions` | int | Number of actions in the largest hunk |
| `avg_group_actions` | float | Mean number of actions per hunk |
| `pct_groups_with_delete` | float | Fraction of hunks containing at least one delete action |
| `groups_with_control_flow` | int | Count of hunks that touch at least one control-flow node |

---

## 6. Security Features (`_security_features`)

Heuristics aimed at vulnerability-related semantics. **Only extracted when `vulnerable_code` and `patched_code` strings are passed to `extract()`** (not used in the default pipeline).

> **Note:** Some of these are intentionally broad proxies. For example, `deleted_boundary_check` fires on deletion of any `if`/`binary_expression` near a comparison operator, not a precise bounds-check motif.

| Feature | Type | Description |
|---|---|---|
| `touches_memory_ops` | bool | Any action label is in `{malloc, calloc, realloc, free}` |
| `deleted_memory_op` | bool | A memory op was deleted |
| `inserted_memory_op` | bool | A memory op was inserted |
| `deleted_boundary_check` | bool | Comparison operators (`<`, `<=`, `>`, `>=`) appear in vulnerable snippets but not patched |
| `inserted_boundary_check` | bool | Comparison operators appear in patched snippets but not vulnerable |
| `added_null_check` | bool | `NULL` appears in patched snippets but not vulnerable |
| `removed_null_check` | bool | `NULL` appears in vulnerable snippets but not patched |

---

## 7. Span Text Features (`_span_text_features`)

Lexical features computed over the raw code text at changed spans. **Only extracted when code strings are passed.**

| Feature | Type | Description |
|---|---|---|
| `avg_changed_snippet_len` | float | Mean character length of changed code snippets |
| `max_changed_snippet_len` | int | Max character length of a changed snippet |
| `snippet_has_NULL` | bool | `NULL` appears in any changed snippet |
| `snippet_has_memcpy` | bool | `memcpy` appears in any changed snippet |
| `snippet_has_strcpy` | bool | `strcpy` appears in any changed snippet |
| `snippet_has_bounds_op` | bool | Any of `<`, `<=`, `>`, `>=` appears in any changed snippet |

---

## 8. Label Vocabulary (`_top_label_vocab_features`)

Bag-of-labels over identifier names touched by actions. **Disabled by default** (`include_label_vocab=False`) because it can cause significant dimensionality expansion.

| Feature | Type | Description |
|---|---|---|
| `label_<name>` | int | Count of times label `<name>` was touched (top-30 labels, sanitized for column safety) |
