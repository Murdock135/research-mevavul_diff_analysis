class add_1 {
final void add(CharSequence name, String... values) {
        final AsciiString normalizedName = normalizeName(name);
        requireNonNull(values, "values");
        final int h = normalizedName.hashCode();
        final int i = index(h);
        for (String v : values) {
            requireNonNullElement(values, v);
            add0(h, i, normalizedName, v);
        }
    }
}
