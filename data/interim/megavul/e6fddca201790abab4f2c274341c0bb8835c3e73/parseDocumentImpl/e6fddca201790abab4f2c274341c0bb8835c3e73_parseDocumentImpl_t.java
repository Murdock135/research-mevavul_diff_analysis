class parseDocumentImpl {
protected static Document parseDocumentImpl(
        InputSource inputSource, boolean enableExternalEntities)
        throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(isNamespaceAware);
        enableOrDisableExternalEntityParsing(factory, enableExternalEntities);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(inputSource);
        return document;
    }
}
