class getAlwaysUsedExtensions {
public Set<DocumentReference> getAlwaysUsedExtensions()
    {
        XWikiContext context = Utils.getContext();
        // Retrieve the current wiki name from the XWiki context
        String currentWiki = StringUtils.defaultIfEmpty(context.getWikiId(), context.getMainXWiki());
        // If we already have extensions defined for this wiki, we return them
        if (this.alwaysUsedExtensions.get(currentWiki) != null) {
            return this.alwaysUsedExtensions.get(currentWiki);
        } else {
            // Otherwise, we look them up in the database.
            Set<DocumentReference> extensions = new HashSet<>();
            String query =
                ", BaseObject as obj, StringProperty as use where obj.className='" + getExtensionClassName() + "'"
                    + " and obj.name=doc.fullName and use.id.id=obj.id and use.id.name='use' and use.value='always'";
            try {
                for (DocumentReference extension : context.getWiki().getStore()
                    .searchDocumentReferences(query, context)) {
                    try {
                        XWikiDocument doc = context.getWiki().getDocument(extension, context);
                        // Only add the extension as being "always used" if the page holding it has been saved with
                        // programming rights.
                        if (getAuthorizationManager().hasAccess(Right.PROGRAM,
                            doc.getAuthorReference(), doc.getDocumentReference())) {
                            extensions.add(extension);
                        }
                    } catch (XWikiException e1) {
                        LOGGER.error("Error while adding skin extension [{}] as always used. It will be ignored.",
                            extension, e1);
                    }
                }
                this.alwaysUsedExtensions.put(currentWiki, extensions);
                return extensions;
            } catch (XWikiException e) {
                LOGGER.error("Error while retrieving always used JS extensions", e);
                return Collections.emptySet();
            }
        }
    }
}
