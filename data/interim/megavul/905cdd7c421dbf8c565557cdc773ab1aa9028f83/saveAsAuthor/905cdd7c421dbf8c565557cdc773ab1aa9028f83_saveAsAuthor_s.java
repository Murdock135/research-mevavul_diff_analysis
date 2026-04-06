class saveAsAuthor {
public void saveAsAuthor(String comment, boolean minorEdit) throws XWikiException
    {
        XWikiContext xcontext = getXWikiContext();

        getAuthors()
            .setOriginalMetadataAuthor(getCurrentUserReferenceResolver().resolve(CurrentUserReference.INSTANCE));
        DocumentReference author = getEffectiveAuthorReference();
        if (hasAccess(Right.EDIT, author)) {
            DocumentReference currentUser = xcontext.getUserReference();
            try {
                xcontext.setUserReference(author);

                saveDocument(comment, minorEdit);
            } finally {
                xcontext.setUserReference(currentUser);
            }
        } else {
            java.lang.Object[] args = { author, xcontext.getDoc(), getFullName() };
            throw new XWikiException(XWikiException.MODULE_XWIKI_ACCESS, XWikiException.ERROR_XWIKI_ACCESS_DENIED,
                "Access denied; user {0}, acting through script in document {1} cannot save document {2}", null, args);
        }
    }
}
