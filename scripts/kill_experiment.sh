#!/usr/bin/env bash
# Kill all Python worker processes belonging to the current project's venv.
# Use this to cleanly abort a running tune_bn1 (or any other) experiment.

VENV_PYTHON="$(dirname "$0")/../.venv/bin/python3"
VENV_PYTHON="$(realpath "$VENV_PYTHON")"

PIDS=$(pgrep -f "$VENV_PYTHON")

if [ -z "$PIDS" ]; then
    echo "No experiment processes found."
    exit 0
fi

echo "Found processes:"
ps -o pid,pcpu,cmd -p $PIDS --no-headers
echo ""
read -r -p "Kill all ${#PIDS[@]} processes? [y/N] " confirm
if [[ "$confirm" =~ ^[Yy]$ ]]; then
    kill $PIDS
    echo "Sent SIGTERM to: $PIDS"
else
    echo "Aborted."
fi
