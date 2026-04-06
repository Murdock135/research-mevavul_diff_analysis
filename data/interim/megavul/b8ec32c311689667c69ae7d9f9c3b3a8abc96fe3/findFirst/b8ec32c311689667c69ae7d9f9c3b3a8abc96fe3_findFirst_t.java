class findFirst {
default Optional<String> findFirst(CharSequence name) {
        return getFirst(name, ConversionContext.STRING);
    }
}
