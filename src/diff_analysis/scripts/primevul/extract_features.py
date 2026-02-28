# scripts/02_extract_gumtree_features.py

from pathlib import Path
import pandas as pd
import logging
from tqdm import tqdm

from diff_analysis.parsers.gumtree_parser import GumTreeDiffParser
from diff_analysis.feature_extractors.gumtree import GumTreeFeatureExtractor
from diff_analysis.utils.config_utils import find_project_root

logging.basicConfig(
    level=logging.DEBUG,
    format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
    handlers=[
        logging.FileHandler("gumtree_feature_extraction.log"),
        logging.StreamHandler(),
    ],
)
logger = logging.getLogger(__name__)


def extract_features_from_xmls(
    xml_dir: Path,
    output_path: Path,
    include_group_features: bool = True,
    include_label_vocab: bool = False,
) -> pd.DataFrame:
    """
    Extract features from all GumTree XML files in a directory.

    Args:
        xml_dir: Directory containing GumTree XML diff files
        output_path: Where to save the resulting CSV
        include_group_features: Whether to include group/hunk features
        include_label_vocab: Whether to include top-K label vocabulary

    Returns:
        DataFrame with extracted features
    """

    # Find all XML files
    xml_files = list(xml_dir.rglob("*.xml"))
    logger.info(f"Found {len(xml_files)} XML files in {xml_dir}")

    if not xml_files:
        logger.warning("No XML files found!")
        return pd.DataFrame()

    # Initialize parser and extractor
    parser = GumTreeDiffParser()
    extractor = GumTreeFeatureExtractor()

    # Process each file
    results = []
    errors = []

    for xml_path in tqdm(xml_files, desc="Extracting features"):
        try:
            # Parse the diff
            logger.info("Parsing XML: %s", xml_path)
            diff = parser.parse(xml_path)

            # Extract features
            logger.info("Extracting features from diff: %s", xml_path)
            features = extractor.extract(
                diff,
                include_group_features=include_group_features,
                include_label_vocab=include_label_vocab,
            )

            # Add metadata
            features["xml_path"] = str(xml_path.relative_to(xml_dir))
            features["xml_name"] = xml_path.name

            results.append(features)

        except Exception as e:
            logger.error(f"Error processing {xml_path}: {e}")
            errors.append({"xml_path": str(xml_path), "error": str(e)})

    logger.info(f"Successfully processed {len(results)}/{len(xml_files)} files")
    logger.info(f"Errors: {len(errors)}")

    # Create DataFrame
    df = pd.DataFrame(results)

    # Fill NaNs with zeros
    df.fillna(0, inplace=True)

    # Save results
    output_path.parent.mkdir(parents=True, exist_ok=True)
    df.to_csv(output_path, index=False)
    logger.info(f"Saved features to {output_path}")

    # Save errors if any
    if errors:
        error_path = output_path.parent / f"{output_path.stem}_errors.csv"
        pd.DataFrame(errors).to_csv(error_path, index=False)
        logger.info(f"Saved errors to {error_path}")

    return df


def main():
    # Set paths
    project_root = find_project_root()
    xml_dir = project_root / "data" / "interim" / "coming_data"
    output_path = project_root / "data" / "processed" / "gumtree_features.csv"

    logger.info(f"Project root: {project_root}")
    logger.info(f"XML directory: {xml_dir}")
    logger.info(f"Output path: {output_path}")

    # Extract features
    df = extract_features_from_xmls(
        xml_dir=xml_dir,
        output_path=output_path,
        include_group_features=True,
        include_label_vocab=False,  # Set to True if you want label vocab
    )

    # Print summary
    logger.info(f"\nFeature extraction complete!")
    logger.info(f"Total samples: {len(df)}")
    logger.info(f"Total features: {len(df.columns)}")
    logger.info(f"\nFeature columns:\n{list(df.columns)}")


if __name__ == "__main__":
    main()
