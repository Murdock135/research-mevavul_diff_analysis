class of {
public static AsciiString of(AsciiString name) {
        final AsciiString lowerCased = name.toLowerCase();
        final AsciiString cached = map.get(lowerCased);
        if (cached != null) {
            return cached;
        }

        return validate(lowerCased);
    }
}
