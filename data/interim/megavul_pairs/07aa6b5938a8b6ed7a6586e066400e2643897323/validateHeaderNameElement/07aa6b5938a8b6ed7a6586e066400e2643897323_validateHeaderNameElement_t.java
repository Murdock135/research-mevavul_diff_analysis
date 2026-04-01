class validateHeaderNameElement {
private static void validateHeaderNameElement(byte value) {
        switch (value) {
        case 0x1c:
        case 0x1d:
        case 0x1e:
        case 0x1f:
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
            if (value < 0) {
                throw new IllegalArgumentException("a header name cannot contain non-ASCII character: " + value);
            }
        }
    }
}
