class loadXmlDocument {
@Nonnull
  public static Document loadXmlDocument(@Nonnull final InputStream inStream, @Nullable final String charset, final boolean autoClose) throws SAXException, IOException, ParserConfigurationException {
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setIgnoringComments(true);
    factory.setValidating(false);
    factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

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
