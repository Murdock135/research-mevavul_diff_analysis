class endOfEntryReached {
@Override
  protected void endOfEntryReached(InputStream inputStream) throws IOException {
    verifyContent(readStoredMac(inputStream));
  }
}
