class onDocumentEvent {
private void onDocumentEvent(XWikiDocument document)
    {
        boolean remove = false;
        if (document.getObject(getExtensionClassName()) != null) {
            // new or already existing object
            if (document.getObject(getExtensionClassName(), USE_FIELDNAME, "always", false) != null) {
                if (getAuthorizationManager().hasAccess(Right.PROGRAM,
                    document.getAuthorReference(), document.getDocumentReference())) {
                    getAlwaysUsedExtensions().add(document.getDocumentReference());

                    return;
                } else {
                    // in case the extension lost its programming rights upon this save.
                    remove = true;
                }
            } else {
                // remove if exists but use onDemand
                remove = true;
            }
        } else if (document.getOriginalDocument().getObject(getExtensionClassName()) != null) {
            // object removed
            remove = true;
        }

        if (remove) {
            getAlwaysUsedExtensions().remove(document.getDocumentReference());
        }
    }
}
