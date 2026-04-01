class maybeAddRequestId {
private static String maybeAddRequestId(final String ref, final HttpServletRequest request) {
        final Optional<String> headerName =
                REQUEST_ID_HEADERS.stream().filter(h -> request.getHeader(h) != null).findFirst();
        return headerName.map(
            s -> ref + "@" + request.getHeader(s).replaceAll("[^a-zA-Z0-9._:-]", "_")
        ).orElse(ref);
    }
}
