class initialValue {
@Override
    protected DocumentBuilder initialValue() {
      try {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder();
      } catch (ParserConfigurationException exc) {
        throw new IllegalArgumentException(exc);
      }
    }
}
