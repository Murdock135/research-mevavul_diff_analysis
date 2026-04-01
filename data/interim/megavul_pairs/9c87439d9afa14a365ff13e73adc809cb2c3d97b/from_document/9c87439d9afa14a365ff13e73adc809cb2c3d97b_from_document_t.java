class from_document {
@JRubyMethod(meta=true, required=1, optional=1)
    public static IRubyObject from_document(ThreadContext context, IRubyObject klazz, IRubyObject[] args) {
        IRubyObject document = args[0];
        IRubyObject parseOptions = null;
        if (args.length > 1) {
            parseOptions = args[1];
        }

        XmlDocument doc = ((XmlDocument) ((XmlNode) document).document(context));

        RubyArray errors = (RubyArray) doc.getInstanceVariable("@errors");
        if (!errors.isEmpty()) {
            throw ((XmlSyntaxError) errors.first()).toThrowable();
        }

        DOMSource source = new DOMSource(doc.getDocument());

        IRubyObject uri = doc.url(context);

        if (!uri.isNil()) {
            source.setSystemId(uri.convertToString().asJavaString());
        }

        return getSchema(context, (RubyClass)klazz, source, parseOptions);
    }
}
