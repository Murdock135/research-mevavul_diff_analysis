class getDocument {
private Document getDocument(final InputStream input) throws SAXException {
    try {
      final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setValidating(true);
      // get the dtd location
      final String dtdFile = "/games/strategy/engine/xml/" + DTD_FILE_NAME;
      final URL url = GameParser.class.getResource(dtdFile);
      if (url == null) {
        throw new RuntimeException(String.format("Map: %s, Could not find in classpath %s", mapName, dtdFile));
      }
      final DocumentBuilder builder = factory.newDocumentBuilder();
      builder.setErrorHandler(new ErrorHandler() {
        @Override
        public void fatalError(final SAXParseException exception) {
          errorsSax.add(exception);
        }

        @Override
        public void error(final SAXParseException exception) {
          errorsSax.add(exception);
        }

        @Override
        public void warning(final SAXParseException exception) {
          errorsSax.add(exception);
        }
      });
      final String dtdSystem = url.toExternalForm();
      final String system = dtdSystem.substring(0, dtdSystem.length() - 8);
      return builder.parse(input, system);
    } catch (final IOException | ParserConfigurationException e) {
      throw new IllegalStateException("Error parsing: " + mapName, e);
    }
  }
}
