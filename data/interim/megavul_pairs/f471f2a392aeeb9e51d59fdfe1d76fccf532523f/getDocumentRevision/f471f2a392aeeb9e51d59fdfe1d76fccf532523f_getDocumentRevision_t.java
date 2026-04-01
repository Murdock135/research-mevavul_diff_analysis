class getDocumentRevision {
public Document getDocumentRevision(String revision)
    {
        try {
            DocumentRevisionProvider revisionProvider = getDocumentRevisionProvider();
            revisionProvider.checkAccess(Right.VIEW, CurrentUserReference.INSTANCE, getDocumentReference(), revision);
            XWikiDocument documentRevision = revisionProvider.getRevision(this.doc, revision);

            return documentRevision != null ? new Document(documentRevision, this.context) : null;
        } catch (AuthorizationException e) {
            LOGGER.info("Access denied for loading revision [{}] of document [{}]: [{}]", revision,
                getDocumentReferenceWithLocale(), ExceptionUtils.getRootCauseMessage(e));
        } catch (Exception e) {
            LOGGER.error("Failed to load revision [{}] of document [{}]", revision, getDocumentReferenceWithLocale(),
                e);
        }

        return null;
    }
}
