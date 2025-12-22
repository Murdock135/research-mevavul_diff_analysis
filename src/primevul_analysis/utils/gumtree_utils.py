import pandas as pd

from primevul_analysis.types import GumTreeDiffResult
from primevul_analysis.utils.str_utils import _truncate

def results_to_dataframe(results: list[GumTreeDiffResult]) -> pd.DataFrame:
    """Convert GumTree diff results to a pandas DataFrame."""
    
    data = []
    for result in results:
        data.append({
            'truncated_diff_output': _truncate(result.diff_output, limit=500),
            'diff_path': str(result.diff_path) if result.diff_path else None,
            'file1': str(result.file1),
            'file2': str(result.file2),
            'returncode': result.returncode,
            'has_error': result.error is not None,
            'error': result.error,
            'diff_length': len(result.diff_output) if result.diff_output else 0,
            # Add other fields you want
        })
    
    return pd.DataFrame(data)