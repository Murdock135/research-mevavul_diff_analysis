class validateHeaderNameElement_1 {
private static void validateHeaderNameElement(char value) {
        switch (value) {
        case 0x00:
        case '\t':
        case '\n':
        case 0x0b:
        case '\f':
        case '\r':
        case ' ':
        case ',':
        case ':':
        case ';':
        case '=':
            throw new IllegalArgumentException(
               "a header name cannot contain the following prohibited characters: =,;: \\t\\r\\n\\v\\f: " +
                       value);
        default:
            // Check to see if the character is not an ASCII character, or invalid
            if (value > 127) {
                throw new IllegalArgumentException("a header name cannot contain non-ASCII character: " +
                        value);
            }
        }
    }
}
