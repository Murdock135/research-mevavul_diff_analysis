class parse_io {
@JRubyMethod(name = "io", meta = true)
  public static IRubyObject
  parse_io(ThreadContext context,
           IRubyObject klazz,
           IRubyObject data,
           IRubyObject enc)
  {
    //int encoding = (int)enc.convertToInteger().getLongValue();
    final Ruby runtime = context.runtime;
    XmlSaxParserContext ctx = newInstance(runtime, (RubyClass) klazz);
    ctx.initialize(runtime);
    ctx.setIOInputSource(context, data, runtime.getNil());
    return ctx;
  }
}
