class parse_1 {
@Deprecated // v2.15
    public static ObjectNode parse(
            final IOContext ioContext,
            final int options,
            final Reader reader
    ) throws IOException {
        Parser parser = new Parser(new TomlFactory(), ioContext,
                new TomlStreamReadException.ErrorContext(ioContext.contentReference(), null), options, reader);
        try {
            final ObjectNode node = parser.parse();
            assert parser.getNestingDepth() == 0;
            return node;
        } finally {
            parser.lexer.releaseBuffers();
        }
    }
}
