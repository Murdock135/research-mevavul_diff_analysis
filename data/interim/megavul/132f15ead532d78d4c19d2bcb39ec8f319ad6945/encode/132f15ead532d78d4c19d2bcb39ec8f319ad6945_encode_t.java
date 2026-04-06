class encode {
public byte[] encode(final SecretKey hmacKey)
  {
    if (hmacKey == null) {
      throw new IllegalArgumentException("Secret key cannot be null");
    }
    final ByteBuffer bb = ByteBuffer.allocate(length);
    bb.order(ByteOrder.BIG_ENDIAN);
    bb.putInt(VERSION);
    bb.put(ByteUtil.toBytes(keyName));
    bb.put((byte) 0);
    bb.put(ByteUtil.toUnsignedByte(nonce.length));
    bb.put(nonce);
    bb.put(hmac(bb.array(), 0, bb.limit() - HMAC_SIZE));
    return bb.array();
  }
}
