#!/usr/bin/env bash
# Fix file ownership from root to current user

set -euo pipefail

echo "Fixing file permissions..."
sudo chown -R $USER:$USER .

echo "✓ Done! All files now owned by $USER"
