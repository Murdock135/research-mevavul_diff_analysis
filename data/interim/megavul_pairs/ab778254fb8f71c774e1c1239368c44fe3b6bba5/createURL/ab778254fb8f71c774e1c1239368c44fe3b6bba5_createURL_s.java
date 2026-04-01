class createURL {
@Override
    public URL createURL(String spaces, String name, String queryString, String anchor, String wikiId,
        XWikiContext context, FilesystemExportContext exportContext) throws Exception
    {
        // Check if the current user has the right to view the SX file. We do this since this is what would happen
        // in XE when a SX action is called (check done in XWikiAction).
        // Note that we cannot just open an HTTP connection to the SX action here since we wouldn't be authenticated...
        // Thus we have to simulate the same behavior as the SX action...

        List<String> spaceNames = this.legacySpaceResolve.resolve(spaces);
        DocumentReference sxDocumentReference = new DocumentReference(wikiId, spaceNames, name);
        this.authorizationManager.checkAccess(Right.VIEW, sxDocumentReference);

        // Set the SX document as the current document in the XWiki Context since unfortunately the SxSource code
        // uses the current document in the context instead of accepting it as a parameter...
        XWikiDocument sxDocument = context.getWiki().getDocument(sxDocumentReference, context);

        Map<String, Object> backup = new HashMap<>();
        XWikiDocument.backupContext(backup, context);
        try {
            sxDocument.setAsContextDoc(context);
            return processSx(spaceNames, name, queryString, context, exportContext);
        } finally {
            XWikiDocument.restoreContext(backup, context);
        }
    }
}
