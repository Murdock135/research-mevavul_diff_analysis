class readListBegin {
public TList readListBegin() throws TException {
    byte size_and_type = readByte();
    int size = (size_and_type >> 4) & 0x0f;
    if (size == 15) {
      size = readVarint32();
    }
    byte type = getTType(size_and_type);
    ensureContainerHasEnough(size, type);
    return new TList(type, size);
  }
}
