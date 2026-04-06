class encode {
public byte[] encode(final SecretKey hmacKey)
  {
    final ByteBuffer bb = ByteBuffer.allocate(length);
    bb.order(ByteOrder.BIG_ENDIAN);
    bb.putInt(VERSION);
    bb.put(ByteUtil.toBytes(keyName));
    bb.put((byte) 0);
    bb.put(ByteUtil.toUnsignedByte(nonce.length));
    bb.put(nonce);
    if (hmacKey != null) {
      final byte[] hmac = hmac(bb.array(), 0, bb.limit() - HMAC_SIZE);
      bb.put(hmac);
    }
    return bb.array();
  }
}
