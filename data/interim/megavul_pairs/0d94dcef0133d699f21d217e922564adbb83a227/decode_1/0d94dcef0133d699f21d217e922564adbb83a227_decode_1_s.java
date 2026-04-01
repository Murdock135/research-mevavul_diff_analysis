class decode_1 {
public JWT decode(String encodedJWT, Verifier... verifiers) {
    Objects.requireNonNull(encodedJWT);
    Objects.requireNonNull(verifiers);

    // An unsecured JWT will not contain a signature and should only have a header and a payload.
    String[] parts = getParts(encodedJWT);
    Header header = Mapper.deserialize(base64Decode(parts[0].getBytes(StandardCharsets.UTF_8)), Header.class);

    // Be particular about decoding an unsecured JWT. If the JWT is signed or any verifiers were provided don't do it.
    if (header.algorithm == Algorithm.none && parts.length == 2 && verifiers.length == 0) {
      return Mapper.deserialize(base64Decode(parts[1].getBytes(StandardCharsets.UTF_8)), JWT.class);
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
