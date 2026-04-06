class getEmbeddedLwM2mSchema {
protected Schema getEmbeddedLwM2mSchema() throws SAXException {
        InputStream inputStream = DDFFileValidator.class.getResourceAsStream(LWM2M_V1_SCHEMA_PATH);
        Source source = new StreamSource(inputStream);
        SchemaFactory schemaFactory = createSchemaFactory();
        return schemaFactory.newSchema(source);
    }
}
