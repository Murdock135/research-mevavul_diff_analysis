class decrypt {
public static byte[] decrypt(final BlockCipher cipher, final SecretKey key, final byte[] data)
    throws CryptoException, EncodingException
  {
    final CiphertextHeader header = CiphertextHeader.decode(data);
    final PaddedBufferedBlockCipher padded = new PaddedBufferedBlockCipher(cipher, new PKCS7Padding());
    padded.init(false, new ParametersWithIV(new KeyParameter(key.getEncoded()), header.getNonce()));
    return decrypt(new BufferedBlockCipherAdapter(padded), data, header.getLength());
  }
}
