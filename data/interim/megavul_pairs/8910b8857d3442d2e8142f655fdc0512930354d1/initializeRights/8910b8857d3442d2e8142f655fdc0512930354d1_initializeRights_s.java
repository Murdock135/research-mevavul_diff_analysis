class initializeRights {
private boolean initializeRights(XWikiDocument document, String xwikiAdminGroupDocumentReference,
        List<Right> rights)
    {
        boolean updated = false;

        try {
            XWikiContext xwikiContext = this.xcontextProvider.get();
            BaseObject object = document.newXObject(LOCAL_CLASS_REFERENCE, xwikiContext);
            XWikiContext xWikiContext = this.xcontextProvider.get();
            object.set(GROUPS_FIELD_NAME, xwikiAdminGroupDocumentReference, xWikiContext);
            object.set(LEVELS_FIELD_NAME, rights.stream().map(Right::getName).collect(Collectors.toList()),
                xWikiContext);
            object.set(ALLOW_FIELD_NAME, 1, xWikiContext);
            updated = true;
        } catch (XWikiException e) {
            this.logger.error(String.format("Error adding a [%s] object to the document [%s]", LOCAL_CLASS_REFERENCE,
                document.getDocumentReference()));
        }

        return updated;
    }
}
