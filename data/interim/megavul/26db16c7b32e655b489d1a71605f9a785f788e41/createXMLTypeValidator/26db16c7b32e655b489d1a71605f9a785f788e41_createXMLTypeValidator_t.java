class createXMLTypeValidator {
public static XMLTypeValidator createXMLTypeValidator(String xmlSchema) {
      try {
        // create a SchemaFactory capable of understanding WXS schemas
        SchemaFactory factory = createSchemaFactoryInstance();
        // load a WXS schema, represented by a Schema instance
        Source xmlSchemaSource = new StreamSource(new StringReader(xmlSchema));
        return new XMLTypeValidator(factory.newSchema(xmlSchemaSource).newValidator());
      } catch (SAXException e) {
        throw new RuntimeException(e);
      }
    }
}
