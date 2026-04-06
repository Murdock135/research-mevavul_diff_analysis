class parseDocumentImpl {
protected static Document parseDocumentImpl(InputSource inputSource)
        throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(isNamespaceAware);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(inputSource);
        return document;
    }
}
