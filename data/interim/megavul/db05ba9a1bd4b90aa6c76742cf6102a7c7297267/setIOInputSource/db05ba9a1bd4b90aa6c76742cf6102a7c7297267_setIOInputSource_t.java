class setIOInputSource {
public void
  setIOInputSource(ThreadContext context, IRubyObject data, IRubyObject url)
  {
    source = new InputSource();
    ParserContext.setUrl(context, source, url);

    Ruby ruby = context.getRuntime();

    if (!(data.respondsTo("read"))) {
      throw ruby.newTypeError("must respond to :read");
    }

    source.setByteStream(new IOInputStream(data));
    if (java_encoding != null) {
      source.setEncoding(java_encoding);
    }
  }
}
