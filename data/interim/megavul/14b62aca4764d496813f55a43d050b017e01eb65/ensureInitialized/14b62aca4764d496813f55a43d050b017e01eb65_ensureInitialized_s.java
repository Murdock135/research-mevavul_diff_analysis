class ensureInitialized {
private void ensureInitialized() throws SQLException {
    if (!initialized) {
      throw new PSQLException(
          GT.tr(
              "This SQLXML object has not been initialized, so you cannot retrieve data from it."),
          PSQLState.OBJECT_NOT_IN_STATE);
    }

    // Is anyone loading data into us at the moment?
    if (!active) {
      return;
    }

    if (byteArrayOutputStream != null) {
      try {
        data = conn.getEncoding().decode(byteArrayOutputStream.toByteArray());
      } catch (IOException ioe) {
        throw new PSQLException(GT.tr("Failed to convert binary xml data to encoding: {0}.",
            conn.getEncoding().name()), PSQLState.DATA_ERROR, ioe);
      } finally {
        byteArrayOutputStream = null;
        active = false;
      }
    } else if (stringWriter != null) {
      // This is also handling the work for Stream, SAX, and StAX Results
      // as they will use the same underlying stringwriter variable.
      //
      data = stringWriter.toString();
      stringWriter = null;
      active = false;
    } else if (domResult != null) {
      // Copy the content from the result to a source
      // and use the identify transform to get it into a
      // friendlier result format.
      try {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        DOMSource domSource = new DOMSource(domResult.getNode());
        StringWriter stringWriter = new StringWriter();
        StreamResult streamResult = new StreamResult(stringWriter);
        transformer.transform(domSource, streamResult);
        data = stringWriter.toString();
      } catch (TransformerException te) {
        throw new PSQLException(GT.tr("Unable to convert DOMResult SQLXML data to a string."),
            PSQLState.DATA_ERROR, te);
      } finally {
        domResult = null;
        active = false;
      }
    }
  }
}
