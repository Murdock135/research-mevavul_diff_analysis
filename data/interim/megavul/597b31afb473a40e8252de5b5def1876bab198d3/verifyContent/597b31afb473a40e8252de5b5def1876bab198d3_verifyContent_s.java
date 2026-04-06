class verifyContent {
private void verifyContent(byte[] storedMac) throws IOException {
    if (getLocalFileHeader().isDataDescriptorExists()
        && CompressionMethod.DEFLATE.equals(Zip4jUtil.getCompressionMethod(getLocalFileHeader()))) {
      // Skip content verification in case of Deflate compression and if data descriptor exists.
      // In this case, we do not know the exact size of compressed data before hand and it is possible that we read
      // and pass more than required data into inflater, thereby corrupting the aes mac bytes.
      // See usage of PushBackInputStream in the project for how this push back of data is done
      // Unfortunately, in this case we cannot perform a content verification and have to skip
      return;
    }

    byte[] calculatedMac = getDecrypter().getCalculatedAuthenticationBytes();
    byte[] first10BytesOfCalculatedMac = new byte[AES_AUTH_LENGTH];
    System.arraycopy(calculatedMac, 0, first10BytesOfCalculatedMac, 0, InternalZipConstants.AES_AUTH_LENGTH);

    if (!Arrays.equals(storedMac, first10BytesOfCalculatedMac)) {
      throw new IOException("Reached end of data for this entry, but aes verification failed");
    }
  }
}
