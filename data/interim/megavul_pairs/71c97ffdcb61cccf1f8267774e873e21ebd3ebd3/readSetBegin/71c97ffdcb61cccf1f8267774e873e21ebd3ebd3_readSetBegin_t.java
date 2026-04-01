class readSetBegin {
public TSet readSetBegin() throws TException {
    byte type = readByte();
    int size = readI32();
    ensureContainerHasEnough(size, type);
    return new TSet(type, size);
  }
}
