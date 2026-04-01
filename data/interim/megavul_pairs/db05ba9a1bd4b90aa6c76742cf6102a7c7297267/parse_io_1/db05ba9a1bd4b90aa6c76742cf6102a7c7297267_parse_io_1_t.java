class parse_io_1 {
@JRubyMethod(name = "io", meta = true)
  public static IRubyObject
  parse_io(ThreadContext context,
           IRubyObject klass,
           IRubyObject data,
           IRubyObject encoding)
  {
    if (!(encoding instanceof RubyFixnum)) {
      throw context.getRuntime().newTypeError("encoding must be kind_of String");
    }

    Html4SaxParserContext ctx = Html4SaxParserContext.newInstance(context.runtime, (RubyClass) klass);
    ctx.setIOInputSource(context, data, context.nil);
    String javaEncoding = findEncodingName(context, encoding);
    if (javaEncoding != null) {
      ctx.getInputSource().setEncoding(javaEncoding);
    }
    return ctx;
  }
}
