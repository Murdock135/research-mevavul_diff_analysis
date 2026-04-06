class parseXmlSafely {
default Document parseXmlSafely(final int contentLength, final Reader reader) throws WebdavException {
    if (contentLength == 0) {
      return null;
    }

    if (reader == null) {
      // No content?
      return null;
    }

    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setNamespaceAware(false);
      factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
      factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
      factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
      factory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

      DocumentBuilder builder = factory.newDocumentBuilder();

      return builder.parse(new InputSource(reader));
    } catch (SAXException exception) {
      throw new WebdavBadRequest(exception.getMessage());
    } catch (Throwable t) {
      throw new WebdavException(t);
    }
  }
}
