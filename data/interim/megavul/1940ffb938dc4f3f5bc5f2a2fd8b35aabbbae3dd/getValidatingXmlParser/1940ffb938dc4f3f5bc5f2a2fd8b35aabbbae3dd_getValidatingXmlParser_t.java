class getValidatingXmlParser {
public static DocumentBuilder getValidatingXmlParser(File schemaFile) {
    DocumentBuilder db = null;
    try {
      DocumentBuilderFactory dbf = safeDocumentBuilderFactory();

      SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
      Schema schema = factory.newSchema(schemaFile);
      dbf.setSchema(schema);

      db = dbf.newDocumentBuilder();
      db.setErrorHandler(new SAXErrorHandler());

    } catch (ParserConfigurationException e) {
      log.warnf("%s: Unable to create XML parser\n", XMLUtils.class.getName());
      log.warn(e);

    } catch (SAXException e) {
      log.warnf("%s: XML parsing exception while loading schema %s\n", XMLUtils.class.getName(),schemaFile.getPath());
      log.warn(e);

    } catch(UnsupportedOperationException e) {
      log.warnf("%s: API error while setting up XML parser. Check your JAXP version\n", XMLUtils.class.getName());
      log.warn(e);
    }

    return db;
  }
}
