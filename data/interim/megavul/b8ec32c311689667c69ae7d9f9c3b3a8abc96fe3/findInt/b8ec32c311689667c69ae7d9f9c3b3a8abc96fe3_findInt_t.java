class findInt {
default Optional<Integer> findInt(CharSequence name) {
        return get(name, ConversionContext.INT);
    }
}
