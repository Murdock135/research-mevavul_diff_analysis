class restrictToAdmin {
public boolean restrictToAdmin(XWikiDocument document)
    {
        boolean updated = false;
        // If some rights have already been set on the document, we consider that it has already been protected 
        // manually.
        if (document.getXObjects(LOCAL_CLASS_REFERENCE).isEmpty()) {
            updated = initializeRights(document, XWIKI_ADMIN_GROUP_DOCUMENT_REFERENCE, List.of(VIEW, EDIT, DELETE));
        }

        return updated;
    }
}
