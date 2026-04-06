class getPrimitive {
public List<Entry<P>> getPrimitive(final byte[] identifier) {
    List<Entry<P>> found = primitives.get(new Prefix(identifier));
    return found != null ? found : Collections.<Entry<P>>emptyList();
  }
}
