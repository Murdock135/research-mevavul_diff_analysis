class readBracketPropertyToken {
private boolean readBracketPropertyToken(PathTokenAppender appender) {
        if (!path.currentCharIs(OPEN_SQUARE_BRACKET)) {
            return false;
        }
        char potentialStringDelimiter = path.nextSignificantChar();
        if (potentialStringDelimiter != SINGLE_QUOTE && potentialStringDelimiter != DOUBLE_QUOTE) {
          return false;
        }

        List<String> properties = new ArrayList<String>();

        int startPosition = path.position() + 1;
        int readPosition = startPosition;
        int endPosition = 0;
        boolean inProperty = false;
        boolean inEscape = false;
        boolean lastSignificantWasComma = false;

        while (path.inBounds(readPosition)) {
            char c = path.charAt(readPosition);

            if(inEscape){
                inEscape = false;
            } else if('\\' == c){
                inEscape = true;
            } else if (c == CLOSE_SQUARE_BRACKET && !inProperty) {
                if (lastSignificantWasComma){
                  fail("Found empty property at index "+readPosition);
                }
                break;
            } else if (c == potentialStringDelimiter) {
                if (inProperty) {
                    char nextSignificantChar = path.nextSignificantChar(readPosition);
                    if (nextSignificantChar != CLOSE_SQUARE_BRACKET && nextSignificantChar != COMMA) {
                        fail("Property must be separated by comma or Property must be terminated close square bracket at index "+readPosition);
                    }
                    endPosition = readPosition;
                    String prop = path.subSequence(startPosition, endPosition).toString();
                    properties.add(Utils.unescape(prop));
                    inProperty = false;
                } else {
                    startPosition = readPosition + 1;
                    inProperty = true;
                    lastSignificantWasComma = false;
                }
            } else if (c == COMMA && !inProperty) {
                if (lastSignificantWasComma){
                    fail("Found empty property at index "+readPosition);
                }
                lastSignificantWasComma = true;
            }
            readPosition++;
        }

        if (inProperty){
            fail("Property has not been closed - missing closing " + potentialStringDelimiter);
        }

        int endBracketIndex = path.indexOfNextSignificantChar(endPosition, CLOSE_SQUARE_BRACKET) + 1;

        path.setPosition(endBracketIndex);

        appender.appendPathToken(PathTokenFactory.createPropertyPathToken(properties, potentialStringDelimiter));

        return path.currentIsTail() || readNextToken(appender);
    }
}
