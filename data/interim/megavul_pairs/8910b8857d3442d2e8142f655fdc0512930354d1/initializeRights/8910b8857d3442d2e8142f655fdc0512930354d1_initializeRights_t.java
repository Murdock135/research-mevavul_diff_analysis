class initializeRights {
private boolean initializeRights(XWikiDocument document, List<Right> rights)
    {
        boolean updated = false;

        try {
            BaseObject object = document.newXObject(LOCAL_CLASS_REFERENCE, this.xcontextProvider.get());
            setRights(object, rights);
            updated = true;
        } catch (XWikiException e) {
            this.logger.error(String.format("Error adding a [%s] object to the document [%s]", LOCAL_CLASS_REFERENCE,
                document.getDocumentReference()));
        }

        return updated;
    }
}
