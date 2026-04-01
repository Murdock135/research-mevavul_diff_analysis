class getCalculatedAuthenticationBytes {
public byte[] getCalculatedAuthenticationBytes() {
    return mac.doFinal();
  }
}
