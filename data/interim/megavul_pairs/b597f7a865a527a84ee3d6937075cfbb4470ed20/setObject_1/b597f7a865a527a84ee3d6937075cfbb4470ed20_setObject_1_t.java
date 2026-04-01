class setObject_1 {
final void setObject(CharSequence name, Object... values) {
        final AsciiString normalizedName = HttpHeaderNames.of(name);
        requireNonNull(values, "values");

        final int h = normalizedName.hashCode();
        final int i = index(h);

        remove0(h, i, normalizedName);
        for (Object v: values) {
            requireNonNullElement(values, v);
            add0(h, i, normalizedName, fromObject(v));
        }
    }
}
