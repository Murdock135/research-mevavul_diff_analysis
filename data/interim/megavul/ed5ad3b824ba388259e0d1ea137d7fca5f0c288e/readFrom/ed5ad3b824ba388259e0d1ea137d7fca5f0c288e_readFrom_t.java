class readFrom {
@Override
  public T readFrom(Class<T> type, Type genericType, Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
      throws WebApplicationException {
    T t = YamlUtils.readValue(toString(entityStream), type);
    return t;
  }
}
