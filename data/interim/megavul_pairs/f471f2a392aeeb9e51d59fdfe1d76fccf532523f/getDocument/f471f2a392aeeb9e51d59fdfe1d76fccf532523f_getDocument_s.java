class getDocument {
public Document getDocument(DocumentReference reference, String revision) throws XWikiException
    {
        try {
            if (reference != null && getContextualAuthorizationManager().hasAccess(Right.VIEW, reference)) {
                XWikiDocument documentRevision = getDocumentRevisionProvider().getRevision(reference, revision);

                if (documentRevision != null) {
                    return new Document(documentRevision, this.context);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to access revision [{}] of document {}", revision, reference, e);
        }

        return null;
    }
}
