class encrypt {
public static void encrypt(
    final AEADBlockCipher cipher,
    final SecretKey key,
    final Nonce nonce,
    final InputStream input,
    final OutputStream output)
    throws CryptoException, StreamException
  {
    final byte[] iv = nonce.generate();
    final byte[] header = new CiphertextHeaderV2(iv, "1").encode(key);
    cipher.init(true, new AEADParameters(new KeyParameter(key.getEncoded()), MAC_SIZE_BITS, iv, header));
    writeHeader(header, output);
    process(new AEADBlockCipherAdapter(cipher), input, output);
  }
}
