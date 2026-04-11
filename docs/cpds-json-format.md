# CPDs JSON Format

Reference for the `<stem>_cpds.json` file written by `BNPipeline.save_cpd_json()` (called from `analysis/bn1/fit_bn1.py`).

---

## File location

```
data/results/bn1/<stem>/<stem>_cpds.json
```

e.g. `data/results/bn1/bn1_hillclimb_mi50_tabu10_hcs/bn1_hillclimb_mi50_tabu10_hcs_cpds.json`

---

## Top-level structure

The file is a JSON array, one entry per node in the DAG (51 nodes for the mi50 fit).

```json
[ <cpd_entry>, <cpd_entry>, ... ]
```

---

## CPD entry schema

```
{
  "variable":    string,              // node name
  "parents":     string[],            // parent node names (empty for root nodes)
  "state_names": { string: string[] },// possible states for each variable in scope
  "table":       record[]             // probability table (see below)
}
```

### `table` records

Each record represents one row of the conditional probability table — a specific assignment of parent states and the resulting probability distribution over the node's states.

- **Parent columns**: one key per parent, value is the parent's state (always `"0"` or `"1"` for binary features)
- **Probability columns**: one key per node state, formatted as `"<variable>=<state>"`, value is the probability (float, rounded to 8 decimal places)

Rows are enumerated in Cartesian-product order over parent states (first parent varies slowest).

---

## Examples

### Root node (no parents)

```json
{
  "variable": "insert-node_LocalVariable_Block",
  "parents": [],
  "state_names": {
    "insert-node_LocalVariable_Block": ["0", "1"]
  },
  "table": [
    {
      "insert-node_LocalVariable_Block=0": 0.89724902,
      "insert-node_LocalVariable_Block=1": 0.10275098
    }
  ]
}
```

Root nodes have a single table record (the marginal distribution).

### Node with one parent

```json
{
  "variable": "move-tree_BinaryOperator_LocalVariable",
  "parents": ["delete-node_LocalVariable_Block"],
  "state_names": {
    "move-tree_BinaryOperator_LocalVariable": ["0", "1"],
    "delete-node_LocalVariable_Block": ["0", "1"]
  },
  "table": [
    {
      "delete-node_LocalVariable_Block": "0",
      "move-tree_BinaryOperator_LocalVariable=0": 0.99902757,
      "move-tree_BinaryOperator_LocalVariable=1": 0.00097243
    },
    {
      "delete-node_LocalVariable_Block": "1",
      "move-tree_BinaryOperator_LocalVariable=0": 0.96553447,
      "move-tree_BinaryOperator_LocalVariable=1": 0.03446553
    }
  ]
}
```

### Target node (`is_vul`) with three parents

```json
{
  "variable": "is_vul",
  "parents": [
    "insert-node_Assignment_Block",
    "insert-node_If_Block",
    "insert-node_Invocation_Block"
  ],
  "state_names": { "...": ["0", "1"] },
  "table": [
    {
      "insert-node_Assignment_Block": "0",
      "insert-node_If_Block": "0",
      "insert-node_Invocation_Block": "0",
      "is_vul=0": 0.40757416,
      "is_vul=1": 0.59242584
    },
    {
      "insert-node_Assignment_Block": "0",
      "insert-node_If_Block": "0",
      "insert-node_Invocation_Block": "1",
      "is_vul=0": 0.76472878,
      "is_vul=1": 0.23527122
    }
  ]
}
```

---

## Loading downstream

### Load the full array

```python
import json
from pathlib import Path

with open("data/results/bn1/bn1_hillclimb_mi50_tabu10_hcs/bn1_hillclimb_mi50_tabu10_hcs_cpds.json") as f:
    cpds = json.load(f)
```

### Look up a specific node

```python
cpd = next(e for e in cpds if e["variable"] == "is_vul")
```

### Load a node's table into a DataFrame

```python
import pandas as pd

cpd = next(e for e in cpds if e["variable"] == "is_vul")
df = pd.DataFrame(cpd["table"])
```

This gives a tidy DataFrame where parent columns hold state assignments and
probability columns hold the conditional probabilities:

```
  insert-node_Assignment_Block  insert-node_If_Block  ...  is_vul=0  is_vul=1
0                            0                     0  ...  0.407574  0.592426
1                            0                     0  ...  0.764729  0.235271
...
```

### Extract all CPDs into a single long DataFrame

Useful for filtering or plotting across nodes:

```python
import pandas as pd

frames = []
for entry in cpds:
    df = pd.DataFrame(entry["table"])
    df.insert(0, "variable", entry["variable"])
    frames.append(df)

all_cpds = pd.concat(frames, ignore_index=True)
```

### Get P(is_vul=1 | all features present)

```python
cpd = next(e for e in cpds if e["variable"] == "is_vul")
df = pd.DataFrame(cpd["table"])

parents = cpd["parents"]
mask = (df[parents] == "1").all(axis=1)
p_vul = df.loc[mask, "is_vul=1"].values[0]
print(f"P(is_vul=1 | all parents=1) = {p_vul:.4f}")
```
