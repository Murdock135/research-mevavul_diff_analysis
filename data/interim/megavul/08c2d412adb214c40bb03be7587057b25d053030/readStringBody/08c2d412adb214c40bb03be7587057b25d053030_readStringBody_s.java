class readStringBody {
public String readStringBody(int size) throws TException {
    checkReadLength(size);
    byte[] buf = new byte[size];
    trans_.readAll(buf, 0, size);
    return new String(buf, StandardCharsets.UTF_8);
  }
}
