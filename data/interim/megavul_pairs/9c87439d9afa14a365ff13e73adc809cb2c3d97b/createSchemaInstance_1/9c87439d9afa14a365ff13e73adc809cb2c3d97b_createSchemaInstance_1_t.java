class createSchemaInstance_1 {
static XmlSchema createSchemaInstance(ThreadContext context, RubyClass klazz, Source source, IRubyObject parseOptions) {
        Ruby runtime = context.getRuntime();
        XmlRelaxng xmlRelaxng = (XmlRelaxng) NokogiriService.XML_RELAXNG_ALLOCATOR.allocate(runtime, klazz);

        if (parseOptions == null) {
            parseOptions = defaultParseOptions(context.getRuntime());
        }

        xmlRelaxng.setInstanceVariable("@errors", runtime.newEmptyArray());
        xmlRelaxng.setInstanceVariable("@parse_options", parseOptions);

        try {
            Schema schema = xmlRelaxng.getSchema(source, context);
            xmlRelaxng.setVerifier(schema.newVerifier());
            return xmlRelaxng;
        } catch (VerifierConfigurationException ex) {
            throw context.getRuntime().newRuntimeError("Could not parse document: " + ex.getMessage());
        }
    }
}
