class restrictToAdmin {
public boolean restrictToAdmin(XWikiDocument document)
    {
        boolean updated = false;
        List<Right> rights = List.of(VIEW, EDIT, DELETE);

        if (fixBadlyInitializedRights(document, rights)) {
            updated = true;
        }

        // If some rights have already been set on the document, we consider that it has already been protected 
        // manually.
        if (document.getXObjects(LOCAL_CLASS_REFERENCE).isEmpty()) {
            updated = updated || initializeRights(document, rights);
        }

        return updated;
    }
}
