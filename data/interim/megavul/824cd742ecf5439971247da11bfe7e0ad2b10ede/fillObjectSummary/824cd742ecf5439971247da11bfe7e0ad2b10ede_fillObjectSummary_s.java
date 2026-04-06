class fillObjectSummary {
private void fillObjectSummary(ObjectSummary objectSummary, Document doc, BaseObject xwikiObject,
        Boolean withPrettyNames)
    {
        objectSummary.setId(String.format("%s:%s", doc.getPrefixedFullName(), xwikiObject.getGuid()));
        objectSummary.setGuid(xwikiObject.getGuid());
        objectSummary.setPageId(doc.getPrefixedFullName());
        objectSummary.setPageVersion(doc.getVersion());
        objectSummary.setPageAuthor(doc.getAuthor());
        if (withPrettyNames) {
            XWikiContext xwikiContext = this.xcontextProvider.get();
            objectSummary
                .setPageAuthorName(xwikiContext.getWiki().getUserName(doc.getAuthor(), null, false, xwikiContext));
        }
        objectSummary.setWiki(doc.getWiki());
        objectSummary.setSpace(doc.getSpace());
        objectSummary.setPageName(doc.getDocumentReference().getName());
        objectSummary.setClassName(xwikiObject.getClassName());
        objectSummary.setNumber(xwikiObject.getNumber());

        String[] propertyNames = xwikiObject.getPropertyNames();
        if (propertyNames.length > 0) {
            try {
                objectSummary.setHeadline(serializePropertyValue(xwikiObject.get(propertyNames[0])));
            } catch (XWikiException e) {
                // Should never happen
            }
        }
    }
}
