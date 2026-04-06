class add_2 {
final void add(CharSequence name, Iterable<String> values) {
        final AsciiString normalizedName = HttpHeaderNames.of(name);
        requireNonNull(values, "values");
        final int h = normalizedName.hashCode();
        final int i = index(h);
        for (String v : values) {
            requireNonNullElement(values, v);
            add0(h, i, normalizedName, v);
        }
    }
}
