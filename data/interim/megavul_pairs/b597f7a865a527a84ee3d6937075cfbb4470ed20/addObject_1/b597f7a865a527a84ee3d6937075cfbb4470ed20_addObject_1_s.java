class addObject_1 {
final void addObject(CharSequence name, Iterable<?> values) {
        final AsciiString normalizedName = normalizeName(name);
        requireNonNull(values, "values");
        for (Object v : values) {
            requireNonNullElement(values, v);
            addObject(normalizedName, v);
        }
    }
}
