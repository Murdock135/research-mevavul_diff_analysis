from pathlib import Path

def find_project_root() -> Path:
    PROJECT_ROOT = next(
        p for p in Path(__file__).resolve().parents if (p / "pyproject.toml").exists()
    )

    return PROJECT_ROOT