class throwTokenInvalidException {
private void throwTokenInvalidException(final HttpServletRequest request)
      throws TokenValidationException {
    String now = TemporalFormatter.toBaseIso8601(OffsetDateTime.now(), true);
    final TokenValidationException exception = new TokenValidationException(
        "Attempt of a CSRF attack detected at " + now);
    logger.error("The request for path {0} isn''t valid: {1}", request.getRequestURI(),
        exception.getMessage());
    throw exception;
  }
}
