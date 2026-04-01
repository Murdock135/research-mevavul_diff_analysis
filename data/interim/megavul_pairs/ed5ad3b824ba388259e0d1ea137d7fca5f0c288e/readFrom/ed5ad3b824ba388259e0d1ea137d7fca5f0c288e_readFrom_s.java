class readFrom {
@Override
  public T readFrom(Class<T> type, Type genericType, Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
      throws WebApplicationException {
    Yaml yaml = new Yaml(new SafeConstructor(new LoaderOptions()));
    T t = yaml.loadAs(toString(entityStream), type);
    return t;
  }
}
