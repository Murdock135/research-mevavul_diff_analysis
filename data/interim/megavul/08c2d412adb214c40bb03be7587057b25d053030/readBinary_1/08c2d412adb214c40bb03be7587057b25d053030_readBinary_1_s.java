class readBinary_1 {
public byte[] readBinary() throws TException {
    int size = readI32();
    checkReadLength(size);
    byte[] buf = new byte[size];
    trans_.readAll(buf, 0, size);
    return buf;
  }
}
