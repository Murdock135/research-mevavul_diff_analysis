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
                String firstPropertyName = propertyNames[0];
                BaseClass baseClass = xwikiObject.getXClass(this.xcontextProvider.get());
                PropertyInterface field = baseClass.getField(firstPropertyName);
                // The property might not exist in the class. But if it does, it will be a PropertyClass. 
                if (field != null) {
                    String classType = ((com.xpn.xwiki.objects.classes.PropertyClass) field).getClassType();
                    objectSummary.setHeadline(cleanupBeforeMakingPublic(classType, xwikiObject.get(firstPropertyName)));
                } else {
                    objectSummary.setHeadline(serializePropertyValue(xwikiObject.get(firstPropertyName)));
                }
            } catch (XWikiException e) {
                // Should never happen
            }
        }
    }
}
