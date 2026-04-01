class createDocumentImpl {
protected static Document createDocumentImpl(
        String name, String namespaceURI, boolean enableExternalEntities)
        throws ParserConfigurationException, FactoryConfigurationError
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(isNamespaceAware);
        enableOrDisableExternalEntityParsing(factory, enableExternalEntities);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        Element rootElement = null;
        if (namespaceURI != null && namespaceURI.length() > 0) {
            rootElement = document.createElementNS(namespaceURI, name);
        } else {
            rootElement = document.createElement(name);
        }
        document.appendChild(rootElement);
        return document;
    }
}
