class getRevision {
@Override
    public XWikiDocument getRevision(DocumentReference reference, String revision) throws XWikiException
    {
        XWikiContext xcontext = this.xcontextProvider.get();

        XWikiDeletedDocument deletedDocument = xcontext.getWiki().getDeletedDocument(Long.valueOf(revision), xcontext);

        // Only local the document if it matches the asked document reference
        if (deletedDocument != null
            && (reference == null || deletedDocument.getDocumentReference().equals(reference))) {
            return deletedDocument.restoreDocument(xcontext);
        }

        return null;

    }
}
