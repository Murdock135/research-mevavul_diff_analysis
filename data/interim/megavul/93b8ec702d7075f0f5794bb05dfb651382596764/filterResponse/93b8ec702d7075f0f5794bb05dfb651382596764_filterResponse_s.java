class filterResponse {
protected void filterResponse(QueryResponse response, List<DocumentReference> usersToCheck)
    {
        SolrDocumentList results = response.getResults();
        long numFound = results.getNumFound();

        // Since we are modifying the results collection, we need to iterate over its copy.
        for (SolrDocument result : new ArrayList<SolrDocument>(results)) {
            try {
                DocumentReference resultDocumentReference = this.solrDocumentReferenceResolver.resolve(result);

                if (!isAllowed(resultDocumentReference, usersToCheck)) {

                    // Remove the current incompatible result.
                    results.remove(result);

                    // Decrement the number of results.
                    numFound--;

                    // FIXME: We should update maxScore as well when removing the top scored item. How do we do that?
                    // Sorting based on score might be a not so expensive option.

                    // FIXME: What about highlighting, facets and all the other data inside the QueryResponse?
                }
            } catch (Exception e) {
                this.logger.warn("Skipping bad result: {}", result, e);
            }
        }

        // Update the new number of results, excluding the filtered ones.
        if (numFound < 0) {
            // Lower bound guard for the total number of results.
            numFound = 0;
        }
        results.setNumFound(numFound);
    }
}
