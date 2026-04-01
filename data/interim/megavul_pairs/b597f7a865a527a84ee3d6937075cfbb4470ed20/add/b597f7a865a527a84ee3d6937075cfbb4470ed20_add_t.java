class add {
final void add(CharSequence name, String value) {
        final AsciiString normalizedName = HttpHeaderNames.of(name);
        requireNonNull(value, "value");
        final int h = normalizedName.hashCode();
        final int i = index(h);
        add0(h, i, normalizedName, value);
    }
}
