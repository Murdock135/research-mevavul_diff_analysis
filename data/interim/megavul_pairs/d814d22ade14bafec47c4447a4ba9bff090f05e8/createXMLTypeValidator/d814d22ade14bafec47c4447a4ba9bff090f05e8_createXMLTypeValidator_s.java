class createXMLTypeValidator {
public static XMLTypeValidator createXMLTypeValidator(String xmlSchema) {
      // create a SchemaFactory capable of understanding WXS schemas
      SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

      // load a WXS schema, represented by a Schema instance
      Source xmlSchemaSource = new StreamSource(new StringReader(xmlSchema));
      try {
        return new XMLTypeValidator(factory.newSchema(xmlSchemaSource).newValidator());
      } catch (SAXException e) {
        e.printStackTrace();
        return null;
      }
    }
}
