class decrypt_1 {
public static void decrypt(
    final BlockCipher cipher,
    final SecretKey key,
    final InputStream input,
    final OutputStream output)
    throws CryptoException, EncodingException, StreamException
  {
    final CiphertextHeader header = decodeHeader(input, String -> key);
    final PaddedBufferedBlockCipher padded = new PaddedBufferedBlockCipher(cipher, new PKCS7Padding());
    padded.init(false, new ParametersWithIV(new KeyParameter(key.getEncoded()), header.getNonce()));
    process(new BufferedBlockCipherAdapter(padded), input, output);
  }
}
