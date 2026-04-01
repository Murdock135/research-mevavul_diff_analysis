class loadXmlDocument {
@Nonnull
  public static Document loadXmlDocument(@Nonnull final InputStream inStream, @Nullable final String charset, final boolean autoClose) throws SAXException, IOException, ParserConfigurationException {
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    try {
      factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    } catch (final ParserConfigurationException ex) {
      LOGGER.error("Can't set feature for XML parser : " + ex.getMessage(), ex);
      throw new SAXException("Can't set flag to use security processing of XML file");
    }

    try {
      factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
      factory.setFeature("http://apache.org/xml/features/validation/schema", false);
    } catch (final ParserConfigurationException ex) {
      LOGGER.warn("Can't set some features for XML parser : " + ex.getMessage());
    }

    factory.setIgnoringComments(true);
    factory.setValidating(false);

    final DocumentBuilder builder = factory.newDocumentBuilder();

    final Document document;
    try {
      final InputStream stream;
      if (charset == null) {
        stream = inStream;
      } else {
        stream = new ByteArrayInputStream(IOUtils.toString(inStream, charset).getBytes("UTF-8"));
      }
      document = builder.parse(stream);
    } finally {
      if (autoClose) {
        IOUtils.closeQuietly(inStream);
      }
    }

    return document;
  }
}
