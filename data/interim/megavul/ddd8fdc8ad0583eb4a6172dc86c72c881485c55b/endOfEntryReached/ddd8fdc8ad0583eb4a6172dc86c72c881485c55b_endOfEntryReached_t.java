class endOfEntryReached {
@Override
  protected void endOfEntryReached(InputStream inputStream, int numberOfBytesPushedBack) throws IOException {
    verifyContent(readStoredMac(inputStream), numberOfBytesPushedBack);
  }
}
