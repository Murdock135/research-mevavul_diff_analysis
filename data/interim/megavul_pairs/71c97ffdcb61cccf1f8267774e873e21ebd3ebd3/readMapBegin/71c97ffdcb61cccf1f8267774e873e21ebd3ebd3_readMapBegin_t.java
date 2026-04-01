class readMapBegin {
public TMap readMapBegin() throws TException {
    int size = readVarint32();
    byte keyAndValueType = size == 0 ? 0 : readByte();
    byte keyType = getTType((byte) (keyAndValueType >> 4));
    byte valueType = getTType((byte) (keyAndValueType & 0xf));
    if (size > 0) {
      ensureMapHasEnough(size, keyType, valueType);
    }
    return new TMap(keyType, valueType, size);
  }
}
