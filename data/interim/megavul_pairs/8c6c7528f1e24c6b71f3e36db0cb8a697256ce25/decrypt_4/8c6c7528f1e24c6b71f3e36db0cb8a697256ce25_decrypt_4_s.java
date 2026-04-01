class decrypt_4 {
@Override
  public void decrypt(final InputStream input, final OutputStream output)
      throws CryptoException, EncodingException, StreamException
  {
    final CiphertextHeader header = CiphertextHeader.decode(input);
    if (header.getKeyName() == null) {
      throw new CryptoException("Ciphertext header does not contain required key");
    }
    process(header, false, input, output);
  }
}
