class encrypt_3 {
public static byte[] encrypt(final AEADBlockCipher cipher, final SecretKey key, final Nonce nonce, final byte[] data)
    throws CryptoException
  {
    final byte[] iv = nonce.generate();
    final byte[] header = new CiphertextHeaderV2(iv, "1").encode(key);
    cipher.init(true, new AEADParameters(new KeyParameter(key.getEncoded()), MAC_SIZE_BITS, iv, header));
    return encrypt(new AEADBlockCipherAdapter(cipher), header, data);
  }
}
