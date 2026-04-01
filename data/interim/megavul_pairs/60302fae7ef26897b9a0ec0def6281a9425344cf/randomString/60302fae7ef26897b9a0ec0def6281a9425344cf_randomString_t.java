class randomString {
private static String randomString(int byteLength) {
    byte[] bytes = new byte[byteLength];
    SECURE_RANDOM.nextBytes(bytes);
    return new String(bytes, StandardCharsets.ISO_8859_1);
  }
}
