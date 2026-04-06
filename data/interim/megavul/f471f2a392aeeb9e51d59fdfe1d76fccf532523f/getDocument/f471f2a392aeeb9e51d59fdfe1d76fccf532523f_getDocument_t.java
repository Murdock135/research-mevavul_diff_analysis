class getDocument {
public Document getDocument(DocumentReference reference, String revision) throws XWikiException
    {
        try {
            if (reference != null && getContextualAuthorizationManager().hasAccess(Right.VIEW, reference)) {
                DocumentRevisionProvider revisionProvider = getDocumentRevisionProvider();
                revisionProvider.checkAccess(Right.VIEW, CurrentUserReference.INSTANCE, reference, revision);
                XWikiDocument documentRevision = revisionProvider.getRevision(reference, revision);

                if (documentRevision != null) {
                    return new Document(documentRevision, this.context);
                }
            }
        } catch (AuthorizationException e) {
            LOGGER.info("Access denied for loading revision [{}] of document [{}]: [{}]", revision, reference,
                ExceptionUtils.getRootCauseMessage(e));
        } catch (Exception e) {
            LOGGER.error("Failed to access revision [{}] of document {}", revision, reference, e);
        }

        return null;
    }
}
