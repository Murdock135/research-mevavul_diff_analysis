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
            if (TomlReadFeature.VALIDATE_NESTING_DEPTH.enabledIn(options) && parser.getNestingDepth() > 0) {
                throw new IOException("Nesting Depth is non-zero after parsing TOML");
            }
            return node;
        } finally {
            parser.lexer.releaseBuffers();
        }
    }
}
