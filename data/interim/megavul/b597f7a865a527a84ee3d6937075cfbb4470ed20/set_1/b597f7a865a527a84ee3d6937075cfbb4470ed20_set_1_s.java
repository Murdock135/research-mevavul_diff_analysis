class set_1 {
final void set(CharSequence name, String... values) {
        final AsciiString normalizedName = normalizeName(name);
        requireNonNull(values, "values");

        final int h = normalizedName.hashCode();
        final int i = index(h);

        remove0(h, i, normalizedName);
        for (String v : values) {
            requireNonNullElement(values, v);
            add0(h, i, normalizedName, v);
        }
    }
}
