class hasPageExtensions {
@Override
    public boolean hasPageExtensions(XWikiContext context)
    {
        XWikiDocument doc = context.getDoc();
        boolean result = false;
        if (doc != null && this.hasCurrentPageExtensionObjects(doc)) {
            if (getAuthorizationManager().hasAccess(Right.SCRIPT, doc.getAuthorReference(),
                doc.getDocumentReference())) {
                result = true;
            } else {
                displayScriptRightLog(doc.getDocumentReference());
            }
        }
        return result;
    }
}
