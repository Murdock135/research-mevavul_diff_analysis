class getDocBuilder {
private static synchronized DocumentBuilder getDocBuilder() throws ParserConfigurationException {
        if (docBuilderFactory == null)
            initDocBuilderFactory();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        docBuilder.setEntityResolver(new EntityResolver() {
            public InputSource resolveEntity(String publicId, String systemId) {
                if ("-//Apple Computer//DTD PLIST 1.0//EN".equals(publicId) || // older publicId
                        "-//Apple//DTD PLIST 1.0//EN".equals(publicId)) { // newer publicId
                    // return a dummy, zero length DTD so we don't have to fetch
                    // it from the network.
                    return new InputSource(new ByteArrayInputStream(new byte[0]));
                }
                return null;
            }
        });
        return docBuilder;
    }
}
