class encrypt_2 {
public static byte[] encrypt(final BlockCipher cipher, final SecretKey key, final Nonce nonce, final byte[] data)
    throws CryptoException
  {
    final byte[] iv = nonce.generate();
    final byte[] header = new CiphertextHeaderV2(iv, "1").encode(key);
    final PaddedBufferedBlockCipher padded = new PaddedBufferedBlockCipher(cipher, new PKCS7Padding());
    padded.init(true, new ParametersWithIV(new KeyParameter(key.getEncoded()), iv));
    return encrypt(new BufferedBlockCipherAdapter(padded), header, data);
  }
}
