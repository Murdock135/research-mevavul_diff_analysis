class decode_1 {
public JWT decode(String encodedJWT, Verifier... verifiers) {
    Objects.requireNonNull(encodedJWT);
    Objects.requireNonNull(verifiers);

    // An unsecured JWT will not contain a signature and should only have a header and a payload.
    String[] parts = getParts(encodedJWT);
    Header header = Mapper.deserialize(base64Decode(parts[0].getBytes(StandardCharsets.UTF_8)), Header.class);

    // If parts.length == 2 we have no signature, if no verifiers were provided, decode if header says 'none', else throw an exception
    if (parts.length == 2 && verifiers.length == 0) {
      if (header.algorithm == Algorithm.none) {
        return Mapper.deserialize(base64Decode(parts[1].getBytes(StandardCharsets.UTF_8)), JWT.class);
      } else {
        throw new InvalidJWTSignatureException();
      }
    }

    // If verifiers were provided, ensure it is able to verify this JWT.
    Verifier verifier = null;
    for (Verifier v : verifiers) {
      if (v.canVerify(header.algorithm)) {
        verifier = v;
      }
    }

    return decode(encodedJWT, header, parts, verifier);
  }
}
