class addSynchronously {
public void addSynchronously(MediaPackage mediaPackage)
      throws SearchException, IllegalArgumentException, UnauthorizedException, NotFoundException,
      SearchServiceDatabaseException {
    if (mediaPackage == null) {
      throw new IllegalArgumentException("Unable to add a null mediapackage");
    }
    final String mediaPackageId = mediaPackage.getIdentifier().toString();
    logger.debug("Attempting to add media package {} to search index", mediaPackageId);
    AccessControlList acl = authorizationService.getActiveAcl(mediaPackage).getA();

    AccessControlList seriesAcl = persistence.getAccessControlLists(mediaPackage.getSeries(), mediaPackageId).stream()
        .reduce(new AccessControlList(acl.getEntries()), AccessControlList::mergeActions);
    logger.debug("Updating series with merged access control list: {}", seriesAcl);

    Date now = new Date();

    try {
      if (indexManager.add(mediaPackage, acl, seriesAcl, now)) {
        logger.info("Added media package `{}` to the search index, using ACL `{}`", mediaPackageId, acl);
      } else {
        logger.warn("Failed to add media package {} to the search index", mediaPackageId);
      }
    } catch (SolrServerException e) {
      throw new SearchException(e);
    }

    try {
      persistence.storeMediaPackage(mediaPackage, acl, now);
    } catch (SearchServiceDatabaseException e) {
      throw new SearchException(
          String.format("Could not store media package to search database %s", mediaPackageId), e);
    }
  }
}
