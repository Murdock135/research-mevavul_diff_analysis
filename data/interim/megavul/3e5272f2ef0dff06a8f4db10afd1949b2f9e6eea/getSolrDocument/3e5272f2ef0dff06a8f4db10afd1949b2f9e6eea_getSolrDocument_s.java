class getSolrDocument {
private LengthSolrInputDocument getSolrDocument(EntityReference reference)
        throws SolrIndexerException, IllegalArgumentException, ExecutionContextException
    {
        SolrMetadataExtractor metadataExtractor = getMetadataExtractor(reference.getType());

        // If the entity type is supported, use the extractor to get the SolrInputDocuent.
        if (metadataExtractor != null) {
            return metadataExtractor.getSolrDocument(reference);
        }

        return null;
    }
}
