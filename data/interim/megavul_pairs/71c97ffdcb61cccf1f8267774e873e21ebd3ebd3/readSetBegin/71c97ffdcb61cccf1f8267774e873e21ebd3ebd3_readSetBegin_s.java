class readSetBegin {
public TSet readSetBegin() throws TException {
    return new TSet(readByte(), readI32());
  }
}
