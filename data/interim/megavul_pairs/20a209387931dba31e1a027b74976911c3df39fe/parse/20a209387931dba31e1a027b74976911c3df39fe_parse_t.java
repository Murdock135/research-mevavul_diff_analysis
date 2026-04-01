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
            if (factory.isEnabled(TomlReadFeature.VALIDATE_NESTING_DEPTH) && parser.getNestingDepth() > 0) {
                throw new IOException("Nesting Depth is non-zero after parsing TOML");
            }
            return node;
        } finally {
            parser.lexer.releaseBuffers();
        }
    }
}
