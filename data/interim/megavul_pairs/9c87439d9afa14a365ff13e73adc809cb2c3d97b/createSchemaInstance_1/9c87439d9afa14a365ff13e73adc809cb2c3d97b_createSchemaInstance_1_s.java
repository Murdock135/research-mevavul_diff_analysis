class createSchemaInstance_1 {
static XmlSchema createSchemaInstance(ThreadContext context, RubyClass klazz, Source source) {
        Ruby runtime = context.getRuntime();
        XmlRelaxng xmlRelaxng = (XmlRelaxng) NokogiriService.XML_RELAXNG_ALLOCATOR.allocate(runtime, klazz);
        xmlRelaxng.setInstanceVariable("@errors", runtime.newEmptyArray());
        
        try {
            Schema schema = xmlRelaxng.getSchema(source, context);
            xmlRelaxng.setVerifier(schema.newVerifier());
            return xmlRelaxng;
        } catch (VerifierConfigurationException ex) {
            throw context.getRuntime().newRuntimeError("Could not parse document: " + ex.getMessage());
        }
    }
}
