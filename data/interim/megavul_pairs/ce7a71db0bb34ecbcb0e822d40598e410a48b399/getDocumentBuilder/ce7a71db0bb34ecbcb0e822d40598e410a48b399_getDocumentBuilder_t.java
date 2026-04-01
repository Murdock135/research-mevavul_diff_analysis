class getDocumentBuilder {
protected DocumentBuilder getDocumentBuilder(
    ) throws ServiceException {
        DocumentBuilder documentBuilder = null;
        DocumentBuilderFactory documentBuilderFactory = null;
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        // Flags required to prevent XML eXternal Entity injection (XXE)
        try {
        	documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        } catch (ParserConfigurationException e) {
            throw new ServiceException(e);
        }
        try {
        	documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        } catch (ParserConfigurationException e) {
            throw new ServiceException(e);
        }
        try {
        	documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        } catch (ParserConfigurationException e) {
            throw new ServiceException(e);
        }
        try {
        	documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (ParserConfigurationException e) {
            throw new ServiceException(e);
        }
        try {
        	documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new ServiceException(e);
        }
        return documentBuilder;
    }
}
