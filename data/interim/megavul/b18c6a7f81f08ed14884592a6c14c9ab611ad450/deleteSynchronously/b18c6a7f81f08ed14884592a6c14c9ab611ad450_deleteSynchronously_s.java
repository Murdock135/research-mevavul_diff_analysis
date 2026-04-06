class deleteSynchronously {
public boolean deleteSynchronously(String mediaPackageId) throws SearchException, UnauthorizedException,
          NotFoundException {
    SearchResult result;
    try {
      result = solrRequester.getForWrite(new SearchQuery().withId(mediaPackageId));
      if (result.getItems().length == 0) {
        logger.warn(
                "Can not delete mediapackage {}, which is not available for the current user to delete from the search index.",
                mediaPackageId);
        return false;
      }
      logger.info("Removing mediapackage {} from search index", mediaPackageId);

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

      return indexManager.delete(mediaPackageId, now);
    } catch (SolrServerException e) {
      logger.info("Could not delete media package with id {} from search index", mediaPackageId);
      throw new SearchException(e);
    }
  }
}
