class contentLength {
default OptionalLong contentLength() {
        Optional<Long> optional = getFirst(HttpHeaders.CONTENT_LENGTH, Long.class);
        return optional.map(OptionalLong::of).orElseGet(OptionalLong::empty);
    }
}
