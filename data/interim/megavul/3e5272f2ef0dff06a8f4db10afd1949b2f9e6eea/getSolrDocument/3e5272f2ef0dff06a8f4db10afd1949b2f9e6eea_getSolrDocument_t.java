class getSolrDocument {
private LengthSolrInputDocument getSolrDocument(EntityReference reference)
        throws SolrIndexerException, IllegalArgumentException, ExecutionContextException
    {
        SolrMetadataExtractor metadataExtractor = getMetadataExtractor(reference.getType());

        // If the entity type is supported, use the extractor to get the SolrInputDocuent.
        if (metadataExtractor != null) {
            // Set the document that belongs to the entity reference as context document to ensure that the correct
            // settings are loaded for the current document/wiki.
            XWikiContext context = this.xWikiContextProvider.get();
            try {
                XWikiDocument document = context.getWiki().getDocument(reference, context);

                return this.documentContextExecutor.call(() -> metadataExtractor.getSolrDocument(reference), document);
            } catch (SolrIndexerException | IllegalArgumentException e) {
                // Re-throw to avoid wrapping exceptions that are declared in the method signature.
                throw e;
            } catch (Exception e) {
                throw new SolrIndexerException("Error executing the indexer in the context of the document to index",
                    e);
            }
        }

        return null;
    }
}
