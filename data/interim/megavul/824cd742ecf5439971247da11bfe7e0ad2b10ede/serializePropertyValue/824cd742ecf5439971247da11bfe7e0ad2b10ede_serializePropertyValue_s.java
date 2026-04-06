class serializePropertyValue {
private String serializePropertyValue(PropertyInterface property,
        com.xpn.xwiki.objects.classes.PropertyClass propertyClass, XWikiContext context)
    {
        if (propertyClass instanceof ComputedFieldClass) {
            // TODO: the XWikiDocument needs to be explicitely set in the context, otherwise method
            // PropertyClass.renderInContext fires a null pointer exception: bug?
            XWikiDocument document = context.getDoc();
            try {
                context.setDoc(property.getObject().getOwnerDocument());
                ComputedFieldClass computedFieldClass = (ComputedFieldClass) propertyClass;
                return computedFieldClass.getComputedValue(propertyClass.getName(), "", property.getObject(), context);
            } catch (Exception e) {
                logger.error("Error while computing property value [{}] of [{}]", propertyClass.getName(),
                    property.getObject(), e);
                return serializePropertyValue(property);
            } finally {
                // Reset the context document to its original value, even if an exception is raised.
                context.setDoc(document);
            }
        } else {
            return serializePropertyValue(property);
        }
    }
}
