class decode_1 {
public static CiphertextHeader decode(final byte[] data) throws EncodingException
  {
    final ByteBuffer bb = ByteBuffer.wrap(data);
    bb.order(ByteOrder.BIG_ENDIAN);

    final int length = bb.getInt();
    if (length < 0) {
      throw new EncodingException("Invalid ciphertext header length: " + length);
    }

    final byte[] nonce;
    int nonceLen = 0;
    try {
      nonceLen = bb.getInt();
      nonce = new byte[nonceLen];
      bb.get(nonce);
    } catch (IndexOutOfBoundsException | BufferUnderflowException e) {
      throw new EncodingException("Invalid nonce length: " + nonceLen);
    }

    String keyName = null;
    if (length > nonce.length + 8) {
      final byte[] b;
      int keyLen = 0;
      try {
        keyLen = bb.getInt();
        b = new byte[keyLen];
        bb.get(b);
        keyName = new String(b);
      } catch (IndexOutOfBoundsException | BufferUnderflowException e) {
        throw new EncodingException("Invalid key length: " + keyLen);
      }
    }

    return new CiphertextHeader(nonce, keyName);
  }
}
