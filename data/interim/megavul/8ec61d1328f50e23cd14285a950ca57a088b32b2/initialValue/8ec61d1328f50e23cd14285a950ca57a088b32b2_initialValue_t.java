class initialValue {
@Override
    protected DocumentBuilder initialValue() {
      try {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setExpandEntityReferences(false);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        return factory.newDocumentBuilder();
      } catch (ParserConfigurationException exc) {
        throw new IllegalArgumentException(exc);
      }
    }
}
