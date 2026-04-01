class decrypt_5 {
@Override
  public byte[] decrypt(final byte[] input) throws CryptoException, EncodingException
  {
    final CiphertextHeader header = CiphertextHeader.decode(input);
    if (header.getKeyName() == null) {
      throw new CryptoException("Ciphertext header does not contain required key");
    }
    return process(header, false, input);
  }
}
