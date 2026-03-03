#!/usr/bin/env bash
# Zip up data/results/{figures,tables} into data/results/zipped/<timestamp>.zip
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
RESULTS_DIR="$PROJECT_ROOT/data/results"
ZIP_DIR="$RESULTS_DIR/zipped"

mkdir -p "$ZIP_DIR"

TIMESTAMP="$(date +%y-%m-%d_%H-%M)"
ZIP_FILE="$ZIP_DIR/results_${TIMESTAMP}.zip"

cd "$RESULTS_DIR"
zip -r "$ZIP_FILE" figures/ tables/

echo "Saved: $ZIP_FILE"
echo "Size:  $(du -sh "$ZIP_FILE" | cut -f1)"
