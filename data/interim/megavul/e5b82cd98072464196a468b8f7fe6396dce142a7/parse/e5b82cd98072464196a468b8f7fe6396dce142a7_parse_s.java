class parse {
@Override
    public XDOM parse(String content, Syntax syntax) throws ParseException, MissingParserException
    {
        return getParser(syntax).parse(new StringReader(content == null ? "" : content));
    }
}
