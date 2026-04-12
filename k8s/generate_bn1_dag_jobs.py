#!/usr/bin/env python3
from __future__ import annotations

import argparse
import textwrap
from pathlib import Path
from typing import Any

DEFAULT_GRID = {
    "mi_threshold": [150, 200],
    "tabu_length": [10, 50, 100],
    "max_indegree": [None, 3, 5],
}

DEFAULT_HCS = {
    "hcs_delta": 0.05,
    "hcs_c": 0.05,
    "hcs_max_restarts": 50,
}

DEFAULT_OUTPUT_DIR_PREFIX = "results/tune_bn1"
DEFAULT_NAMESPACE = "gp-engine-mizzou-diff-analysis"
DEFAULT_IMAGE = "python:3.13-slim"
DEFAULT_REPO_MOUNT = "/workspace"


def slug_for_indegree(indegree: Any) -> str:
    return "none" if indegree is None else str(indegree)


def build_job_name(mi_threshold: int, tabu_length: int, max_indegree: Any, prefix: str) -> str:
    indeg_slug = slug_for_indegree(max_indegree)
    return f"{prefix}-mi{mi_threshold}-tabu{tabu_length}-indeg{indeg_slug}"


def build_command(repo_mount: str, mi_threshold: int, tabu_length: int, max_indegree: Any, output_dir_prefix: str) -> str:
    args = [
        "python analysis/bn1/learn_dag_isvul.py",
        "--method hillclimb",
        "--scoring-method bic-d",
        f"--mi-threshold {mi_threshold}",
        f"--tabu-length {tabu_length}",
        f"--hcs-delta {DEFAULT_HCS['hcs_delta']}",
        f"--hcs-c {DEFAULT_HCS['hcs_c']}",
        f"--hcs-max-restarts {DEFAULT_HCS['hcs_max_restarts']}",
        f"--output-dir {output_dir_prefix}/{mi_threshold}_tabu{tabu_length}_indeg{slug_for_indegree(max_indegree)}",
    ]
    if max_indegree is not None:
        args.append(f"--max-indegree {max_indegree}")

    command = textwrap.dedent(f"""
        set -e
        cd {repo_mount}
        {"\n        ".join(args)}
    """)
    return command.strip()


def render_job_manifest(name: str, namespace: str, image: str, command: str, mount_path: str | None, pvc_name: str | None) -> str:
    lines = [
        "apiVersion: batch/v1",
        "kind: Job",
        "metadata:",
        f"  name: {name}",
        f"  namespace: {namespace}",
        "spec:",
        "  backoffLimit: 1",
        "  template:",
        "    spec:",
        "      restartPolicy: Never",
        "      containers:",
        f"        - name: {name}-ctr",
        f"          image: {image}",
        "          command:",
        "            - /bin/bash",
        "            - -lc",
        "            - |",
    ]
    lines.extend(_indent(command, 14).splitlines())

    if mount_path and pvc_name:
        lines.extend([
            "          volumeMounts:",
            "            - name: workspace",
            f"              mountPath: {mount_path}",
            "      volumes:",
            "        - name: workspace",
            "          persistentVolumeClaim:",
            f"            claimName: {pvc_name}",
        ])

    return "\n".join(lines) + "\n"


def _indent(text: str, spaces: int) -> str:
    indent = " " * spaces
    return "\n".join(indent + line if line.strip() else line for line in text.splitlines())


def parse_list(value: str) -> list[str]:
    return [item.strip() for item in value.split(",") if item.strip()]


def parse_int_or_none(value: str) -> int | None:
    if value.lower() in {"none", "null", "nil", ""}:
        return None
    return int(value)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Generate Kubernetes Job manifests for BN1 DAG hyperparameter search."
    )
    parser.add_argument("--output-file", type=Path, default=Path("k8s/bn1-dag-jobs.yaml"),
                        help="Path to write the multi-job YAML manifest.")
    parser.add_argument("--namespace", default=DEFAULT_NAMESPACE,
                        help="Kubernetes namespace for the jobs.")
    parser.add_argument("--image", default=DEFAULT_IMAGE,
                        help="Container image that can run the repo and deps.")
    parser.add_argument("--repo-mount-path", default=DEFAULT_REPO_MOUNT,
                        help="Where the repo code is mounted inside the container.")
    parser.add_argument("--workspace-pvc", default=None,
                        help="Optional PVC name to mount the repository workspace.")
    parser.add_argument("--job-prefix", default="bn1-dag",
                        help="Prefix for generated Kubernetes Job names.")
    parser.add_argument("--mi-thresholds", type=parse_list,
                        default=[str(v) for v in DEFAULT_GRID["mi_threshold"]],
                        help="Comma-separated MI thresholds.")
    parser.add_argument("--tabu-lengths", type=parse_list,
                        default=[str(v) for v in DEFAULT_GRID["tabu_length"]],
                        help="Comma-separated tabu lengths.")
    parser.add_argument("--max-indegrees", type=parse_list,
                        default=["none", "3", "5"],
                        help="Comma-separated max indegree values; use 'none' for no limit.")
    parser.add_argument("--output-dir-prefix", default=DEFAULT_OUTPUT_DIR_PREFIX,
                        help="Base output directory under data/ for each job.")
    return parser.parse_args()


def main() -> None:
    args = parse_args()
    output_file = args.output_file
    output_file.parent.mkdir(parents=True, exist_ok=True)

    combos = []
    for mi in args.mi_thresholds:
        for tabu in args.tabu_lengths:
            for indeg in args.max_indegrees:
                combos.append((int(mi), int(tabu), parse_int_or_none(indeg)))

    manifests = []
    for mi, tabu, indeg in combos:
        name = build_job_name(mi, tabu, indeg, args.job_prefix)
        command = build_command(args.repo_mount_path, mi, tabu, indeg, args.output_dir_prefix)
        manifest = render_job_manifest(name, args.namespace, args.image, command,
                                       args.repo_mount_path if args.workspace_pvc else None,
                                       args.workspace_pvc)
        manifests.append(manifest)

    output_file.write_text("\n---\n".join(manifests).strip() + "\n")
    print(f"Wrote {len(manifests)} Job manifests to {output_file}")
    print(f"Use: kubectl apply -f {output_file}")


if __name__ == "__main__":
    main()
