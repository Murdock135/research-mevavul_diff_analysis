class setupParserListener {
private void setupParserListener(ParserEnvironment environment, MultiSourceReader multiSourceReader, GraphqlParser parser, GraphqlAntlrToLanguage toLanguage) {
        ParserOptions parserOptions = toLanguage.getParserOptions();
        ParsingListener parsingListener = parserOptions.getParsingListener();
        int maxTokens = parserOptions.getMaxTokens();
        // prevent a billion laugh attacks by restricting how many tokens we allow
        ParseTreeListener listener = new GraphqlBaseListener() {
            int count = 0;

            @Override
            public void visitTerminal(TerminalNode node) {

                final Token token = node.getSymbol();
                parsingListener.onToken(new ParsingListener.Token() {
                    @Override
                    public String getText() {
                        return token == null ? null : token.getText();
                    }

                    @Override
                    public int getLine() {
                        return token == null ? -1 : token.getLine();
                    }

                    @Override
                    public int getCharPositionInLine() {
                        return token == null ? -1 : token.getCharPositionInLine();
                    }
                });

                count++;
                if (count > maxTokens) {
                    throwCancelParseIfTooManyTokens(environment, token, maxTokens, multiSourceReader);
                }
            }
        };
        parser.addParseListener(listener);
    }
}
