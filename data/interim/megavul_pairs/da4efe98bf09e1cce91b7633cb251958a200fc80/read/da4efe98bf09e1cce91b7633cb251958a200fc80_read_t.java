class read {
@Override
    public Directory read(final ImageInputStream input) throws IOException {
        Validate.notNull(input, "input");

        try {
            DocumentBuilderFactory factory = createDocumentBuilderFactory();

            // TODO: Consider parsing using SAX?
            // TODO: Determine encoding and parse using a Reader...
            // TODO: Refactor scanner to return inputstream?
            // TODO: Be smarter about ASCII-NULL termination/padding (the SAXParser aka Xerces DOMParser doesn't like it)...
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new DefaultHandler());
            Document document = builder.parse(new InputSource(IIOUtil.createStreamAdapter(input)));

            String toolkit = getToolkit(document);
            Node rdfRoot = document.getElementsByTagNameNS(XMP.NS_RDF, "RDF").item(0);
            NodeList descriptions = document.getElementsByTagNameNS(XMP.NS_RDF, "Description");

            return parseDirectories(rdfRoot, descriptions, toolkit);
        }
        catch (SAXException e) {
            throw new IIOException(e.getMessage(), e);
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}
