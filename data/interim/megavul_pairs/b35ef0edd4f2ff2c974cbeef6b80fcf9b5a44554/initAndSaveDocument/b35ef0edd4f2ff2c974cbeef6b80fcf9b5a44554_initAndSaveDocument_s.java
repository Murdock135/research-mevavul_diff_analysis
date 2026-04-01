class initAndSaveDocument {
private void initAndSaveDocument(XWikiContext context, XWikiDocument newDocument, String title, String template,
        String parent) throws XWikiException
    {
        XWiki xwiki = context.getWiki();

        // Set the locale and default locale, considering that we're creating the original version of the document
        // (not a translation).
        newDocument.setLocale(Locale.ROOT);
        if (newDocument.getDefaultLocale() == Locale.ROOT) {
            newDocument.setDefaultLocale(xwiki.getLocalePreference(context));
        }

        // Copy the template.
        readFromTemplate(newDocument, template, context);

        // Set the parent field.
        if (!StringUtils.isEmpty(parent)) {
            DocumentReference parentReference = this.currentmixedReferenceResolver.resolve(parent);
            newDocument.setParentReference(parentReference);
        }

        // Set the document title
        if (title != null) {
            newDocument.setTitle(title);
        }

        // Set the author and creator.
        DocumentReference currentUserReference = context.getUserReference();
        newDocument.setAuthorReference(currentUserReference);
        newDocument.setCreatorReference(currentUserReference);

        // Make sure the user is allowed to make this modification
        xwiki.checkSavingDocument(currentUserReference, newDocument, context);

        xwiki.saveDocument(newDocument, context);
    }
}
