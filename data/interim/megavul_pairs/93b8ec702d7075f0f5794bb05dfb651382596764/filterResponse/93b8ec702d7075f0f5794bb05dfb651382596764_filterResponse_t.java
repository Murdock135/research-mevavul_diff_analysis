class filterResponse {
protected void filterResponse(QueryResponse response, List<DocumentReference> usersToCheck)
    {
        SolrDocumentList results = response.getResults();
        long numResults = results.size();

        results.removeIf(result -> {
            boolean keep = false;
            try {
                DocumentReference resultDocumentReference = this.solrDocumentReferenceResolver.resolve(result);

                keep = isAllowed(resultDocumentReference, usersToCheck);
            } catch (Exception e) {
                // Don't take any risk of including a result for which we cannot determine the document reference and
                // thus cannot determine if the given users have access to it or not.
                this.logger.warn("Removing bad result: {}", result, e);
            }

            // FIXME: We should update maxScore as well when removing the top scored item. How do we do that?
            // Sorting based on score might be a not so expensive option.

            // FIXME: What about highlighting, facets and all the other data inside the QueryResponse?

            return !keep;
        });

        long numFilteredResults = numResults - results.size();

        // Update the number of results, excluding the filtered ones.
        // Lower bound guard for the total number of results.
        long numFound = Math.max(0, response.getResults().getNumFound() - numFilteredResults);

        results.setNumFound(numFound);
    }
}
