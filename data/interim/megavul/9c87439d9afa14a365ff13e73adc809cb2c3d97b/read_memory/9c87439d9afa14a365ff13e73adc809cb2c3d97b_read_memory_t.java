class read_memory {
@JRubyMethod(meta=true, required=1, optional=1)
    public static IRubyObject read_memory(ThreadContext context, IRubyObject klazz, IRubyObject[] args) {
        IRubyObject content = args[0];
        IRubyObject parseOptions = null;
        if (args.length > 1) {
            parseOptions = args[1];
        }
        String data = content.convertToString().asJavaString();
        return getSchema(context, (RubyClass) klazz, new StreamSource(new StringReader(data)), parseOptions);
    }
}
