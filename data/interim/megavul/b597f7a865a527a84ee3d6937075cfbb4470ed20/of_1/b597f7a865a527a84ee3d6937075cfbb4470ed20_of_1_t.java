class of_1 {
public static AsciiString of(CharSequence name) {
        if (name instanceof AsciiString) {
            return of((AsciiString) name);
        }

        final String lowerCased = Ascii.toLowerCase(requireNonNull(name, "name"));
        final AsciiString cached = map.get(lowerCased);
        if (cached != null) {
            return cached;
        }

        return validate(AsciiString.cached(lowerCased));
    }
}
