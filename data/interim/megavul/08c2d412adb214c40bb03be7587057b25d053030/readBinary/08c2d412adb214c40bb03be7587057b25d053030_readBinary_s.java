class readBinary {
private byte[] readBinary(int length) throws TException {
    if (length == 0) {
      return new byte[0];
    }

    byte[] buf = new byte[length];
    trans_.readAll(buf, 0, length);
    return buf;
  }
}
