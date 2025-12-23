from pathlib import Path
import pandas as pd
import logging

from primevul_analysis.utils.config_utils import find_project_root

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)


def merge_features_with_labels(
    features_path: Path,
    code_pairs_path: Path,
    output_path: Path
) -> pd.DataFrame:
    
    # Load features
    logger.info(f"Loading features from {features_path}")
    features_df = pd.read_csv(features_path)
    logger.info(f"Loaded {len(features_df)} feature rows")
    
    # Load code pairs
    logger.info(f"Loading code pairs from {code_pairs_path}")
    pairs_df = pd.read_csv(code_pairs_path)
    logger.info(f"Loaded {len(pairs_df)} code pairs")
    
    # ADD THIS: Create sequential index (this is the extraction order)
    pairs_df['extraction_index'] = pairs_df.index
    
    # Extract pair_id from xml_path
    features_df['pair_id'] = features_df['xml_path'].str.extract(r'pair_(\d+)/').astype(int)
    
    # Merge on extraction_index (not id!)
    logger.info("Merging features with labels...")
    merged_df = features_df.merge(
        pairs_df,
        left_on='pair_id',
        right_on='extraction_index',  # CHANGED: use extraction_index
        how='inner',
        validate='1:1'
    )    
    logger.info(f"Merged dataset has {len(merged_df)} rows")
    
    # Check for missing data
    missing_features = len(features_df) - len(merged_df)
    missing_labels = len(pairs_df) - len(merged_df)
    
    if missing_features > 0:
        logger.warning(f"{missing_features} feature rows had no matching code pair")
    if missing_labels > 0:
        logger.warning(f"{missing_labels} code pairs had no matching features")
    
    # Clean up redundant columns
    merged_df = merged_df.drop(columns=['id', 'pair_id'])
    
    # Parse CWE (it's stored as string representation of list)
    import ast
    merged_df['cwe_list'] = merged_df['cwe'].apply(ast.literal_eval)
    merged_df['primary_cwe'] = merged_df['cwe_list'].apply(lambda x: x[0] if x else None)
    
    # Save merged dataset
    logger.info(f"Saving merged dataset to {output_path}")
    output_path.parent.mkdir(parents=True, exist_ok=True)
    merged_df.to_csv(output_path, index=False)
    
    # Print summary
    logger.info("\n=== Merge Summary ===")
    logger.info(f"Total samples: {len(merged_df)}")
    logger.info(f"Total features: {len([c for c in merged_df.columns if c.startswith('n_') or c.startswith('node_') or c.startswith('act_') or c.startswith('touches_') or c.startswith('deleted_') or c.startswith('inserted_')])}")
    logger.info(f"Projects: {merged_df['project'].nunique()}")
    logger.info(f"CVEs: {merged_df['cve'].nunique()}")
    logger.info(f"Unique CWEs: {merged_df['primary_cwe'].nunique()}")
    logger.info(f"\nTop 10 CWEs:")
    logger.info(merged_df['primary_cwe'].value_counts().head(10))
    logger.info(f"\nTop 10 Projects:")
    logger.info(merged_df['project'].value_counts().head(10))
    
    return merged_df


def main():
    # Set paths
    project_root = find_project_root()
    features_path = project_root / "data" / "gumtree_features.csv"
    code_pairs_path = project_root / "data" / "code_pairs.csv"
    output_path = project_root / "data" / "features_with_labels.csv"
    
    logger.info(f"Project root: {project_root}")
    
    # Merge
    merged_df = merge_features_with_labels(
        features_path=features_path,
        code_pairs_path=code_pairs_path,
        output_path=output_path
    )
    
    logger.info("\n✓ Merge complete!")


if __name__ == "__main__":
    main()