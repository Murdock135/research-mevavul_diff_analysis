class deleteSynchronously {
public boolean deleteSynchronously(final String mediaPackageId) throws SearchException {
    SearchResult result;
    try {
      result = solrRequester.getForWrite(new SearchQuery().withId(mediaPackageId));
      if (result.getItems().length == 0) {
        logger.warn(
                "Can not delete mediapackage {}, which is not available for the current user to delete from the search index.",
                mediaPackageId);
        return false;
      }
      final String seriesId = result.getItems()[0].getDcIsPartOf();
      logger.info("Removing media package {} from search index", mediaPackageId);

      Date now = new Date();
      try {
        persistence.deleteMediaPackage(mediaPackageId, now);
        logger.info("Removed mediapackage {} from search persistence", mediaPackageId);
      } catch (NotFoundException e) {
        // even if mp not found in persistence, it might still exist in search index.
        logger.info("Could not find mediapackage with id {} in persistence, but will try remove it from index, anyway.",
                mediaPackageId);
      } catch (SearchServiceDatabaseException e) {
        logger.error("Could not delete media package with id {} from persistence storage", mediaPackageId);
        throw new SearchException(e);
      }

      final boolean success = indexManager.delete(mediaPackageId, now);

      // Update series
      if (seriesId != null) {
        if (persistence.getMediaPackages(seriesId).size() > 0) {
          // Update series acl if there are still episodes in the series
          final AccessControlList seriesAcl = persistence.getAccessControlLists(seriesId).stream()
              .reduce(new AccessControlList(), AccessControlList::mergeActions);
          indexManager.addSeries(seriesId, seriesAcl);

        } else {
          // Remove series if there are no episodes in the series any longer
          indexManager.delete(seriesId, now);
        }
      }

      return success;
    } catch (SolrServerException | SearchServiceDatabaseException e) {
      logger.info("Could not delete media package with id {} from search index", mediaPackageId);
      throw new SearchException(e);
    }
  }
}
