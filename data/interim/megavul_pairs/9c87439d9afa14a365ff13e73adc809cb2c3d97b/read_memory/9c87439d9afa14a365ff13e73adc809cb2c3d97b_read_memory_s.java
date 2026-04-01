class read_memory {
@JRubyMethod(meta=true)
    public static IRubyObject read_memory(ThreadContext context, IRubyObject klazz, IRubyObject content) {
        String data = content.convertToString().asJavaString();
        return getSchema(context, (RubyClass) klazz, new StreamSource(new StringReader(data)));
    }
}
