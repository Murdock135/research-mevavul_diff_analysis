class readMapBegin_1 {
public TMap readMapBegin() throws TException {
    byte keyType = readByte();
    byte valueType = readByte();
    int size = readI32();
    ensureMapHasEnough(size, keyType, valueType);
    return new TMap(keyType, valueType, size);
  }
}
