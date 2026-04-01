class parseFloat {
private JsonNode parseFloat(int nextState) throws IOException {
        String text = lexer.yytext().replace("_", "");
        pollExpected(TomlToken.FLOAT, nextState);
        if (text.endsWith("nan")) {
            return factory.numberNode(Double.NaN);
        } else if (text.endsWith("inf")) {
            return factory.numberNode(text.startsWith("-") ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
        } else {
            try {
                tomlFactory.streamReadConstraints().validateFPLength(text.length());
                BigDecimal dec = NumberInput.parseBigDecimal(
                        text, tomlFactory.isEnabled(StreamReadFeature.USE_FAST_BIG_NUMBER_PARSER));
                return factory.numberNode(dec);
            } catch (NumberFormatException | StreamConstraintsException e) {
                final String reportNum = text.length() <= MAX_CHARS_TO_REPORT ?
                        text :
                        text.substring(0, MAX_CHARS_TO_REPORT) + " [truncated]";
                throw errorContext.atPosition(lexer).invalidNumber(e, reportNum);
            }
        }
    }
}
