class encrypt_4 {
@Override
  public byte[] encrypt(final byte[] input) throws CryptoException
  {
    return process(header(), true, input);
  }
}
