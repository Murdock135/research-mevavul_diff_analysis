class addObject {
final void addObject(CharSequence name, Object... values) {
        final AsciiString normalizedName = HttpHeaderNames.of(name);
        requireNonNull(values, "values");
        for (Object v : values) {
            requireNonNullElement(values, v);
            addObject(normalizedName, v);
        }
    }
}
