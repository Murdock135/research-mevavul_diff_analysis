class encrypt_5 {
@Override
  public void encrypt(final InputStream input, final OutputStream output) throws CryptoException, StreamException
  {
    final CiphertextHeader header = new CiphertextHeader(nonce.generate(), keyAlias);
    try {
      output.write(header.encode());
    } catch (IOException e) {
      throw new StreamException(e);
    }
    process(header, true, input, output);
  }
}
