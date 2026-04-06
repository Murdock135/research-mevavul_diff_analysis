class contentType {
default Optional<MediaType> contentType() {
        return getFirst(HttpHeaders.CONTENT_TYPE, MediaType.class);
    }
}
