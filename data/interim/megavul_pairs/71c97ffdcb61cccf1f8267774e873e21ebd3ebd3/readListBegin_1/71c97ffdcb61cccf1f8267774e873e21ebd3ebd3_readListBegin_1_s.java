class readListBegin_1 {
public TList readListBegin() throws TException {
    return new TList(readByte(), readI32());
  }
}
