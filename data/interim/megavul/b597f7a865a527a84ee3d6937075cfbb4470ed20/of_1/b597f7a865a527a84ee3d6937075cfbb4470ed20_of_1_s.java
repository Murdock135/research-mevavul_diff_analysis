class of_1 {
public static AsciiString of(CharSequence name) {
        if (name instanceof AsciiString) {
            return of((AsciiString) name);
        }

        final String lowerCased = Ascii.toLowerCase(requireNonNull(name, "name"));
        final AsciiString cached = map.get(lowerCased);
        return cached != null ? cached : AsciiString.cached(lowerCased);
    }
}
