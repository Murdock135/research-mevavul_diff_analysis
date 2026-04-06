class decrypt_1 {
public static void decrypt(
    final BlockCipher cipher,
    final SecretKey key,
    final InputStream input,
    final OutputStream output)
    throws CryptoException, EncodingException, StreamException
  {
    final CiphertextHeader header = CiphertextHeader.decode(input);
    final PaddedBufferedBlockCipher padded = new PaddedBufferedBlockCipher(cipher, new PKCS7Padding());
    padded.init(false, new ParametersWithIV(new KeyParameter(key.getEncoded()), header.getNonce()));
    process(new BufferedBlockCipherAdapter(padded), input, output);
  }
}
