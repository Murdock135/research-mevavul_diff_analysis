class decrypt_2 {
public static byte[] decrypt(final AEADBlockCipher cipher, final SecretKey key, final byte[] data)
      throws CryptoException, EncodingException
  {
    final CiphertextHeader header = CiphertextHeader.decode(data);
    final byte[] nonce = header.getNonce();
    final byte[] hbytes = header.encode();
    cipher.init(false, new AEADParameters(new KeyParameter(key.getEncoded()), MAC_SIZE_BITS, nonce, hbytes));
    return decrypt(new AEADBlockCipherAdapter(cipher), data, header.getLength());
  }
}
