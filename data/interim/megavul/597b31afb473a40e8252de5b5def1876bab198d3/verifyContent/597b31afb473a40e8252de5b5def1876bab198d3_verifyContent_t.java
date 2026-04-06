class verifyContent {
private void verifyContent(byte[] storedMac) throws IOException {
    byte[] calculatedMac = getDecrypter().getCalculatedAuthenticationBytes();
    byte[] first10BytesOfCalculatedMac = new byte[AES_AUTH_LENGTH];
    System.arraycopy(calculatedMac, 0, first10BytesOfCalculatedMac, 0, InternalZipConstants.AES_AUTH_LENGTH);

    if (!Arrays.equals(storedMac, first10BytesOfCalculatedMac)) {
      throw new IOException("Reached end of data for this entry, but aes verification failed");
    }
  }
}
