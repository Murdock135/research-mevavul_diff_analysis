class getDocumentBuilder {
protected DocumentBuilder getDocumentBuilder(
    ) throws ServiceException {
        DocumentBuilder documentBuilder = null;
        DocumentBuilderFactory documentBuilderFactory = null;
        try {
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new ServiceException(e);
        }
        return documentBuilder;
    }
}
