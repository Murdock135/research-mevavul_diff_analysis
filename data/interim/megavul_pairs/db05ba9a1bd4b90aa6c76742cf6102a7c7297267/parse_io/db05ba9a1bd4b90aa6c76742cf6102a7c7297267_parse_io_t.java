class parse_io {
@JRubyMethod(name = "io", meta = true)
  public static IRubyObject
  parse_io(ThreadContext context,
           IRubyObject klazz,
           IRubyObject data,
           IRubyObject encoding)
  {
    // check the type of the unused encoding to match behavior of CRuby
    if (!(encoding instanceof RubyFixnum)) {
      throw context.getRuntime().newTypeError("encoding must be kind_of String");
    }
    final Ruby runtime = context.runtime;
    XmlSaxParserContext ctx = newInstance(runtime, (RubyClass) klazz);
    ctx.initialize(runtime);
    ctx.setIOInputSource(context, data, runtime.getNil());
    return ctx;
  }
}
