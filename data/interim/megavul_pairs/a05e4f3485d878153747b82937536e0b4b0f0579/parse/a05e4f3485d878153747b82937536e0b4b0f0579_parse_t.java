class parse {
public static ObjectNode parse(
            final TomlFactory tomlFactory,
            final IOContext ioContext,
            final Reader reader
    ) throws IOException {
        final TomlFactory factory = tomlFactory == null ? new TomlFactory() : tomlFactory;
        Parser parser = new Parser(factory, ioContext,
                new TomlStreamReadException.ErrorContext(ioContext.contentReference(), null),
                factory.getFormatParserFeatures(), reader);
        try {
            final ObjectNode node = parser.parse();
            assert parser.getNestingDepth() == 0;
            return node;
        } finally {
            parser.lexer.releaseBuffers();
        }
    }
}
