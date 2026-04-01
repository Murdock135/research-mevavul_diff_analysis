class readStringBody {
public String readStringBody(int size) throws TException {
    ensureContainerHasEnough(size, TType.BYTE);
    checkReadLength(size);
    byte[] buf = new byte[size];
    trans_.readAll(buf, 0, size);
    return new String(buf, StandardCharsets.UTF_8);
  }
}
