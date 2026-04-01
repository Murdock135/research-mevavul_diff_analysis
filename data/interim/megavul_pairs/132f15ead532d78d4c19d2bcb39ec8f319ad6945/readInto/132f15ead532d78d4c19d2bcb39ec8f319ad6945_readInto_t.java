class readInto {
private static void readInto(final InputStream input, final byte[] output) throws StreamException
  {
    try {
      input.read(output);
    } catch (IOException e) {
      throw new StreamException(e);
    }
  }
}
