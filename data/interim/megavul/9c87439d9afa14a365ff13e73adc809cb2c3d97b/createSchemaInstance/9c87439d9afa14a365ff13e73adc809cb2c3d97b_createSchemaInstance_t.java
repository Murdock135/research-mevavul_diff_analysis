class createSchemaInstance {
static XmlSchema createSchemaInstance(ThreadContext context, RubyClass klazz, Source source, IRubyObject parseOptions) {
        Ruby runtime = context.getRuntime();
        XmlSchema xmlSchema = (XmlSchema) NokogiriService.XML_SCHEMA_ALLOCATOR.allocate(runtime, klazz);

        if (parseOptions == null) {
            parseOptions = defaultParseOptions(context.getRuntime());
        }

        xmlSchema.setInstanceVariable("@errors", runtime.newEmptyArray());
        xmlSchema.setInstanceVariable("@parse_options", parseOptions);

        try {
            SchemaErrorHandler error_handler = new SchemaErrorHandler(context.getRuntime(), (RubyArray)xmlSchema.getInstanceVariable("@errors"));
            Schema schema = xmlSchema.getSchema(source, context.getRuntime().getCurrentDirectory(), context.getRuntime().getInstanceConfig().getScriptFileName(), error_handler);
            xmlSchema.setValidator(schema.newValidator());
            return xmlSchema;
        } catch (SAXException ex) {
            throw context.getRuntime().newRuntimeError("Could not parse document: " + ex.getMessage());
        }
    }
}
