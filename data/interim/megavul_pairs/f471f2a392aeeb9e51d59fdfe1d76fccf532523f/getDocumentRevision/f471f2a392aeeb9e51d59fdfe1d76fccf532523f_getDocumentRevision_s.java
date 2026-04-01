class getDocumentRevision {
public Document getDocumentRevision(String revision)
    {
        try {
            XWikiDocument documentRevision = getDocumentRevisionProvider().getRevision(this.doc, revision);

            return documentRevision != null ? new Document(documentRevision, this.context) : null;
        } catch (Exception e) {
            LOGGER.error("Failed to load revision [{}] of document [{}]", revision, getDocumentReferenceWithLocale(),
                e);

            return null;
        }
    }
}
