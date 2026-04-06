class throwTokenInvalidException {
private void throwTokenInvalidException() throws TokenValidationException {
    String now = TemporalFormatter.toBaseIso8601(OffsetDateTime.now(), true);
    throw new TokenValidationException("Attempt of a CSRF attack detected at " + now);
  }
}
