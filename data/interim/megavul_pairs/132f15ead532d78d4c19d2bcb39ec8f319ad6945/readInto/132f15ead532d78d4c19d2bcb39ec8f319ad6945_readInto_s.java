class readInto {
private static void readInto(final InputStream input, final byte[] output)
  {
    try {
      input.read(output);
    } catch (IOException e) {
      throw new StreamException(e);
    }
  }
}
