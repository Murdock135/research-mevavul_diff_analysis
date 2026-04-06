class readListBegin_1 {
public TList readListBegin() throws TException {
    byte type = readByte();
    int size = readI32();
    ensureContainerHasEnough(size, type);
    return new TList(type, size);
  }
}
