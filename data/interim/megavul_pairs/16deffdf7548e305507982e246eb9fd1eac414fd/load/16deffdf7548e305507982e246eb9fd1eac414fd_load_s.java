class load {
public static ServerXml load(String filename) throws Exception {

        logger.info("ServerXml: Parsing " + filename);

        ServerXml serverXml = new ServerXml();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(filename);

        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();

        NodeList connectors = (NodeList) xpath.evaluate(
                "/Server/Service[@name='Catalina']/Connector",
                document,
                XPathConstants.NODESET);

        int length = connectors.getLength();
        for (int i = 0; i < length; i++) {
            Element connector = (Element) connectors.item(i);

            String protocol = connector.getAttribute("protocol");
            if (protocol.startsWith("AJP/")) {
                continue;
            }

            // HTTP/1.1 connector

            String scheme = connector.getAttribute("scheme");
            String port = connector.getAttribute("port");

            if (scheme != null && scheme.equals("https")) {
                logger.info("ServerXml: Secure port: " + port);
                serverXml.setSecurePort(port);

            } else {
                logger.info("ServerXml: Unsecure port: " + port);
                serverXml.setUnsecurePort(port);
            }
        }

        return serverXml;
    }
}
