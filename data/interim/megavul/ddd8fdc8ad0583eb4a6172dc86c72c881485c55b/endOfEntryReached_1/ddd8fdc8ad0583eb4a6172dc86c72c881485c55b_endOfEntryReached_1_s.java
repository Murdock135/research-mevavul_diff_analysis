class endOfEntryReached_1 {
@Override
  public void endOfEntryReached(InputStream inputStream) throws IOException {
    if (inflater != null) {
      inflater.end();
      inflater = null;
    }
    super.endOfEntryReached(inputStream);
  }
}
