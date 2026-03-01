from pathlib import Path

# TODO:
class ComingChangeFrequencyExtractor:
    def __init__(self, data_dir: str | Path) -> None:
        """Build a feature matrix from
            - change_frequency_<direction>.json
            - cve_id (str)
            - cwe_ids (list[str])
        """
        self.data_dir = Path(data_dir)
        self.feature_matrix = []