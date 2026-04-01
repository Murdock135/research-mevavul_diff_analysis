class getPrimitive {
public List<Entry<P>> getPrimitive(final byte[] identifier) {
    List<Entry<P>> found = primitives.get(new String(identifier, UTF_8));
    return found != null ? found : Collections.<Entry<P>>emptyList();
  }
}
