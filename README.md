# Setup
## Prerequisites
- Install git LFS
- Install [`uv`](https://docs.astral.sh/uv/)

Then,
1. Clone the repo
2. Sync dependencies by running the following 
```bash
uv sync
```
3. Run the scripts as described below.


| Script Path | Purpose | Configuration Method |
| :--- | :--- | :--- |
| `src/diff_analysis/scripts/megavul/bn1/tune_bn1.py` | Runs structure learning on **all** hyperparameter configurations. | Edit **global variables** (block words) within the script. |
| `src/diff_analysis/scripts/megavul/bn1/learn_dag_isvul.py` | Runs structure learning on a **single** hyperparameter configuration. | Use **CLI arguments** (use `--help` for details). |

---

To run the scripts, check the docstrings for help. They are both run with uv as python modules. For example, to run the `tune_bn1.py` script, use the following command:

```bash
uv run src.diff_analysis.scripts.megavul.bn1.tune_bn1
```

To run the `learn_dag_isvul.py` script, use the following command:

```bash
uv run src.diff_analysis.scripts.megavul.bn1.learn_dag_isvul --help
```