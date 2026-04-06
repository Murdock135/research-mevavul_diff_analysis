class getSource {
@Override
  public synchronized <T extends Source> T getSource(Class<T> sourceClass) throws SQLException {
    checkFreed();
    ensureInitialized();

    if (data == null) {
      return null;
    }

    try {
      if (sourceClass == null || DOMSource.class.equals(sourceClass)) {
        DocumentBuilder builder = getXmlFactoryFactory().newDocumentBuilder();
        InputSource input = new InputSource(new StringReader(data));
        return (T) new DOMSource(builder.parse(input));
      } else if (SAXSource.class.equals(sourceClass)) {
        XMLReader reader = getXmlFactoryFactory().createXMLReader();
        InputSource is = new InputSource(new StringReader(data));
        return (T) new SAXSource(reader, is);
      } else if (StreamSource.class.equals(sourceClass)) {
        return (T) new StreamSource(new StringReader(data));
      } else if (StAXSource.class.equals(sourceClass)) {
        XMLInputFactory xif = getXmlFactoryFactory().newXMLInputFactory();
        XMLStreamReader xsr = xif.createXMLStreamReader(new StringReader(data));
        return (T) new StAXSource(xsr);
      }
    } catch (Exception e) {
      throw new PSQLException(GT.tr("Unable to decode xml data."), PSQLState.DATA_ERROR, e);
    }

    throw new PSQLException(GT.tr("Unknown XML Source class: {0}", sourceClass),
        PSQLState.INVALID_PARAMETER_TYPE);
  }
}
