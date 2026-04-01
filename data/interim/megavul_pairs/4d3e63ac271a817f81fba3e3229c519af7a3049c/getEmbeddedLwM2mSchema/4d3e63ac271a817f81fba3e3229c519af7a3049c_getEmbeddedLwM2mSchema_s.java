class getEmbeddedLwM2mSchema {
protected Schema getEmbeddedLwM2mSchema() throws SAXException {
        InputStream inputStream = DDFFileValidator.class.getResourceAsStream(LWM2M_V1_SCHEMA_PATH);
        Source source = new StreamSource(inputStream);
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        return schemaFactory.newSchema(source);
    }
}
