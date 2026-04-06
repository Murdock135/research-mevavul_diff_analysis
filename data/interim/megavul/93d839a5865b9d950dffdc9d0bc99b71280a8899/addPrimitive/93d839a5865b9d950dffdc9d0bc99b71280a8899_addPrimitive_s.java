class addPrimitive {
public Entry<P> addPrimitive(final P primitive, Keyset.Key key)
      throws GeneralSecurityException {
    if (key.getStatus() != KeyStatusType.ENABLED) {
      throw new GeneralSecurityException("only ENABLED key is allowed");
    }
    Entry<P> entry =
        new Entry<P>(
            primitive,
            CryptoFormat.getOutputPrefix(key),
            key.getStatus(),
            key.getOutputPrefixType(),
            key.getKeyId());
    List<Entry<P>> list = new ArrayList<Entry<P>>();
    list.add(entry);
    // Cannot use [] as keys in hash map, convert to string.
    String identifier = new String(entry.getIdentifier(), UTF_8);
    List<Entry<P>> existing = primitives.put(identifier, Collections.unmodifiableList(list));
    if (existing != null) {
      List<Entry<P>> newList = new ArrayList<Entry<P>>();
      newList.addAll(existing);
      newList.add(entry);
      primitives.put(identifier, Collections.unmodifiableList(newList));
    }
    return entry;
  }
}
