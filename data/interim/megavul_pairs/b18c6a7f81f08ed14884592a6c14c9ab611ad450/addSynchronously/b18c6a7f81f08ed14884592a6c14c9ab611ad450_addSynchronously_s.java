class addSynchronously {
public void addSynchronously(MediaPackage mediaPackage) throws SearchException, MediaPackageException,
          IllegalArgumentException, UnauthorizedException {
    if (mediaPackage == null) {
      throw new IllegalArgumentException("Unable to add a null mediapackage");
    }
    logger.debug("Attempting to add mediapackage {} to search index", mediaPackage.getIdentifier());
    AccessControlList acl = authorizationService.getActiveAcl(mediaPackage).getA();

    Date now = new Date();

    try {
      if (indexManager.add(mediaPackage, acl, now)) {
        logger.info("Added mediapackage `{}` to the search index, using ACL `{}`", mediaPackage, acl);
      } else {
        logger.warn("Failed to add mediapackage {} to the search index", mediaPackage.getIdentifier());
      }
    } catch (SolrServerException e) {
      throw new SearchException(e);
    }

    try {
      persistence.storeMediaPackage(mediaPackage, acl, now);
    } catch (SearchServiceDatabaseException e) {
      logger.error("Could not store media package to search database {}: {}", mediaPackage.getIdentifier(), e);
      throw new SearchException(e);
    }
  }
}
