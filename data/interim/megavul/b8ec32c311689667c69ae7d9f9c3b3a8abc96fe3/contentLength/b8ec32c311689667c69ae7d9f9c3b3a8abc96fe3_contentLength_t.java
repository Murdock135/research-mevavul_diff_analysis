class contentLength {
default OptionalLong contentLength() {
        Optional<Long> optional = getFirst(HttpHeaders.CONTENT_LENGTH, ConversionContext.LONG);
        return optional.map(OptionalLong::of).orElseGet(OptionalLong::empty);
    }
}
