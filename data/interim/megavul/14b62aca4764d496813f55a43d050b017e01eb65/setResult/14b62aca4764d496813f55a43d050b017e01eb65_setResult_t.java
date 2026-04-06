class setResult {
@Override
  public synchronized <T extends Result> T setResult(Class<T> resultClass) throws SQLException {
    checkFreed();
    initialize();

    if (resultClass == null || DOMResult.class.equals(resultClass)) {
      domResult = new DOMResult();
      active = true;
      return (T) domResult;
    } else if (SAXResult.class.equals(resultClass)) {
      try {
        SAXTransformerFactory transformerFactory = getXmlFactoryFactory().newSAXTransformerFactory();
        TransformerHandler transformerHandler = transformerFactory.newTransformerHandler();
        stringWriter = new StringWriter();
        transformerHandler.setResult(new StreamResult(stringWriter));
        active = true;
        return (T) new SAXResult(transformerHandler);
      } catch (TransformerException te) {
        throw new PSQLException(GT.tr("Unable to create SAXResult for SQLXML."),
            PSQLState.UNEXPECTED_ERROR, te);
      }
    } else if (StreamResult.class.equals(resultClass)) {
      stringWriter = new StringWriter();
      active = true;
      return (T) new StreamResult(stringWriter);
    } else if (StAXResult.class.equals(resultClass)) {
      stringWriter = new StringWriter();
      try {
        XMLOutputFactory xof = getXmlFactoryFactory().newXMLOutputFactory();
        XMLStreamWriter xsw = xof.createXMLStreamWriter(stringWriter);
        active = true;
        return (T) new StAXResult(xsw);
      } catch (XMLStreamException xse) {
        throw new PSQLException(GT.tr("Unable to create StAXResult for SQLXML"),
            PSQLState.UNEXPECTED_ERROR, xse);
      }
    }

    throw new PSQLException(GT.tr("Unknown XML Result class: {0}", resultClass),
        PSQLState.INVALID_PARAMETER_TYPE);
  }
}
