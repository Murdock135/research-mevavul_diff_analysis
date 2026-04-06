class doFinal_1 {
public byte[] doFinal(byte[] M) {
    if (macCache.size() > 0) {
      doMacUpdate(0);
    }
    return mac.doFinal(M);
  }
}
