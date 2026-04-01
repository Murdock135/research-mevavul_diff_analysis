class encrypt_1 {
public static void encrypt(
    final BlockCipher cipher,
    final SecretKey key,
    final Nonce nonce,
    final InputStream input,
    final OutputStream output)
    throws CryptoException, StreamException
  {
    final byte[] iv = nonce.generate();
    final byte[] header = new CiphertextHeaderV2(iv, "1").encode(key);
    final PaddedBufferedBlockCipher padded = new PaddedBufferedBlockCipher(cipher, new PKCS7Padding());
    padded.init(true, new ParametersWithIV(new KeyParameter(key.getEncoded()), iv));
    writeHeader(header, output);
    process(new BufferedBlockCipherAdapter(padded), input, output);
  }
}
