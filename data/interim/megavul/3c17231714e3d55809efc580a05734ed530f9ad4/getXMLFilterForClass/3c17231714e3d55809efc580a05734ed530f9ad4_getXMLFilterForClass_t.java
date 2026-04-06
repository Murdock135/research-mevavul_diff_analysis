class getXMLFilterForClass {
public static <T> XMLFilter getXMLFilterForClass(final Class<T> clazz, boolean disableDOCTYPE) throws SAXException {
        final String namespace = getNamespaceForClass(clazz);
        XMLFilter filter = namespace == null? new SimpleNamespaceFilter("", false) : new SimpleNamespaceFilter(namespace, true);

        LOG.trace("namespace filter for class {}: {}", clazz, filter);
        final XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        if (disableDOCTYPE) {
            xmlReader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        }
        xmlReader.setFeature("http://xml.org/sax/features/external-general-entities", false);
        xmlReader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        xmlReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        filter.setParent(xmlReader);
        return filter;
    }
}
