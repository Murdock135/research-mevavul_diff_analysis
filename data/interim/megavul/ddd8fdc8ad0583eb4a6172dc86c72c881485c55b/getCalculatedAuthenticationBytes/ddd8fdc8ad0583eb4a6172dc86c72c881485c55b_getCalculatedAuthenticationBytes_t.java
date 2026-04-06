class getCalculatedAuthenticationBytes {
public byte[] getCalculatedAuthenticationBytes(int numberOfBytesPushedBack) {
    return mac.doFinal(numberOfBytesPushedBack);
  }
}
