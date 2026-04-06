class parse {
@Override
    public XDOM parse(String content, Syntax syntax) throws ParseException, MissingParserException
    {
        Parser parser = getParser(syntax);
        try {
            return parser.parse(new StringReader(content == null ? "" : content));
        } catch (StackOverflowError | Exception e) {
            // All exceptions as well as stack overflow errors are captured and wrapped in parse exceptions to make sure
            // that they are handled correctly by the callers. Without this, some parsing issues can be badly handled,
            // leading to instability issues.
            throw new ParseException(String.format("Failed to parse with syntax [%s].", syntax.toIdString()), e);
        }
    }
}
