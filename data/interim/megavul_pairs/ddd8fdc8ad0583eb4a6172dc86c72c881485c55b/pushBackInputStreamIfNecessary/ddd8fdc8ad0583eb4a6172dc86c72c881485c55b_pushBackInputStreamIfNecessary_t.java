class pushBackInputStreamIfNecessary {
@Override
  public int pushBackInputStreamIfNecessary(PushbackInputStream pushbackInputStream) throws IOException {
    int n = inflater.getRemaining();
    if (n > 0) {
      byte[] rawDataCache = getLastReadRawDataCache();
      pushbackInputStream.unread(rawDataCache, len - n, n);
    }
    return n;
  }
}
