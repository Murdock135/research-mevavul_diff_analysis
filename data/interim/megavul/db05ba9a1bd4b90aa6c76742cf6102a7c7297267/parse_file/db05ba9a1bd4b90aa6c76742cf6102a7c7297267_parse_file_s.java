class parse_file {
@JRubyMethod(name = "file", meta = true)
  public static IRubyObject
  parse_file(ThreadContext context,
             IRubyObject klass,
             IRubyObject data,
             IRubyObject encoding)
  {
    Html4SaxParserContext ctx = Html4SaxParserContext.newInstance(context.runtime, (RubyClass) klass);
    ctx.setInputSourceFile(context, data);
    String javaEncoding = findEncodingName(context, encoding);
    if (javaEncoding != null) {
      ctx.getInputSource().setEncoding(javaEncoding);
    }
    return ctx;
  }
}
