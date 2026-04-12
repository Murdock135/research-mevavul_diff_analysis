# Kubernetes BN1 DAG Job Generator

This helper generates one Kubernetes `Job` manifest per BN1 hyperparameter combination.

## Usage

From `research-explaining_vuln_fixes`:

```bash
python k8s/generate_bn1_dag_jobs.py \
  --output-file k8s/bn1-dag-jobs.yaml \
  --namespace gp-engine-mizzou-diff-analysis \
  --image python:3.13-slim \
  --repo-mount-path /workspace \
  --workspace-pvc my-repo-pvc
```

Then apply the generated manifest:

```bash
kubectl apply -f k8s/bn1-dag-jobs.yaml
```

## What it generates

Each job runs:

```bash
cd /workspace
python analysis/bn1/learn_dag_isvul.py --method hillclimb ...
```

It writes outputs under `data/<output-dir>` inside the repository.

## Customization

- `--mi-thresholds`: comma-separated MI feature selection values
- `--tabu-lengths`: comma-separated tabu lengths
- `--max-indegrees`: comma-separated max indegree values; use `none` for no limit
- `--output-dir-prefix`: base output folder under `data/`
