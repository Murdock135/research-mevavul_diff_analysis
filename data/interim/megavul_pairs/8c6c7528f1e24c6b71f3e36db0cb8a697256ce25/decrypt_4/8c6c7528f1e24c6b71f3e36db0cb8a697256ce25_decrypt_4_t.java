class decrypt_4 {
@Override
  public void decrypt(final InputStream input, final OutputStream output)
      throws CryptoException, EncodingException, StreamException
  {
    process(CipherUtil.decodeHeader(input, this::lookupKey), false, input, output);
  }
}
