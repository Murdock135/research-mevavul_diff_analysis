class setStringInputSource {
public void
  setStringInputSource(ThreadContext context, IRubyObject data, IRubyObject url)
  {
    source = new InputSource();
    ParserContext.setUrl(context, source, url);

    Ruby ruby = context.getRuntime();

    if (!(data instanceof RubyString)) {
      throw ruby.newArgumentError("must be kind_of String");
    }

    RubyString stringData = (RubyString) data;

    if (stringData.encoding(context) != null) {
      RubyString stringEncoding = stringData.encoding(context).asString();
      String encName = NokogiriHelpers.getValidEncodingOrNull(stringEncoding);
      if (encName != null) {
        java_encoding = encName;
      }
    }

    ByteList bytes = stringData.getByteList();

    stringDataSize = bytes.length() - bytes.begin();
    ByteArrayInputStream stream = new ByteArrayInputStream(bytes.unsafeBytes(), bytes.begin(), bytes.length());
    source.setByteStream(stream);
    source.setEncoding(java_encoding);
  }
}
