class getDocBuilder {
public static synchronized DocumentBuilder getDocBuilder() throws ParserConfigurationException {
        DocumentBuilder builder = FACTORY.newDocumentBuilder();
        builder.setEntityResolver(new PlistDTDResolver());
        return builder;
    }
}
