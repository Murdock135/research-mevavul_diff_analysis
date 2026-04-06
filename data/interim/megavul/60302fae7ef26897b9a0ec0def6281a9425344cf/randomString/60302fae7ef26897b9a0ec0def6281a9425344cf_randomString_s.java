class randomString {
private static String randomString(int byteLength) {
    byte[] bytes = new byte[byteLength];
    try {
      SecureRandom.getInstanceStrong().nextBytes(bytes);
    } catch (NoSuchAlgorithmException e) {
      throw new UncheckedException(e);
    }
    return new String(bytes, StandardCharsets.ISO_8859_1);
  }
}
