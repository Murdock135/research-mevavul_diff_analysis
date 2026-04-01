class decode_1 {
public static CiphertextHeader decode(final byte[] data) throws EncodingException
  {
    final ByteBuffer bb = ByteBuffer.wrap(data);
    bb.order(ByteOrder.BIG_ENDIAN);

    final int length = bb.getInt();
    if (length < 0) {
      throw new EncodingException("Bad ciphertext header");
    }

    final byte[] nonce;
    int nonceLen = 0;
    try {
      nonceLen = bb.getInt();
      if (nonceLen > MAX_NONCE_LEN) {
        throw new EncodingException("Bad ciphertext header: maximum nonce length exceeded");
      }
      nonce = new byte[nonceLen];
      bb.get(nonce);
    } catch (IndexOutOfBoundsException | BufferUnderflowException e) {
      throw new EncodingException("Bad ciphertext header");
    }

    String keyName = null;
    if (length > nonce.length + 8) {
      final byte[] b;
      int keyLen = 0;
      try {
        keyLen = bb.getInt();
        if (keyLen > MAX_KEYNAME_LEN) {
          throw new EncodingException("Bad ciphertext header: maximum key length exceeded");
        }
        b = new byte[keyLen];
        bb.get(b);
        keyName = new String(b);
      } catch (IndexOutOfBoundsException | BufferUnderflowException e) {
        throw new EncodingException("Bad ciphertext header");
      }
    }

    return new CiphertextHeader(nonce, keyName);
  }
}
