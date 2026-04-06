class readMapBegin_1 {
public TMap readMapBegin() throws TException {
    return new TMap(readByte(), readByte(), readI32());
  }
}
