class decrypt_3 {
public static void decrypt(
    final AEADBlockCipher cipher,
    final SecretKey key,
    final InputStream input,
    final OutputStream output)
    throws CryptoException, EncodingException, StreamException
  {
    final CiphertextHeader header = CiphertextHeader.decode(input);
    final byte[] nonce = header.getNonce();
    final byte[] hbytes = header.encode();
    cipher.init(false, new AEADParameters(new KeyParameter(key.getEncoded()), MAC_SIZE_BITS, nonce, hbytes));
    process(new AEADBlockCipherAdapter(cipher), input, output);
  }
}
