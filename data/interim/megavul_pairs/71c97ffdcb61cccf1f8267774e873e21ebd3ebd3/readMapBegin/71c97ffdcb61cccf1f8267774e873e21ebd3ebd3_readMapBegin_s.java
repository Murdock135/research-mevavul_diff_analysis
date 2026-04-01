class readMapBegin {
public TMap readMapBegin() throws TException {
    int size = readVarint32();
    byte keyAndValueType = size == 0 ? 0 : readByte();
    return new TMap(
        getTType((byte) (keyAndValueType >> 4)), getTType((byte) (keyAndValueType & 0xf)), size);
  }
}
