class decrypt_5 {
@Override
  public byte[] decrypt(final byte[] input) throws CryptoException, EncodingException
  {
    return process(CipherUtil.decodeHeader(input, this::lookupKey), false, input);
  }
}
