class set_2 {
final void set(CharSequence name, String value) {
        final AsciiString normalizedName = normalizeName(name);
        requireNonNull(value, "value");
        final int h = normalizedName.hashCode();
        final int i = index(h);
        remove0(h, i, normalizedName);
        add0(h, i, normalizedName, value);
    }
}
