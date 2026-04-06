class newInstance_1 {
public static SAXParserFactory newInstance() {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        secureProcessing(factory);
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        return factory;
    }
}
