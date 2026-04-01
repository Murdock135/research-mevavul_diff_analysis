class writeTo {
@Override
  public void writeTo(T t, Class<?> type, Type genericType,
      Annotation[] annotations,
      MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
      OutputStream entityStream) throws IOException, WebApplicationException {
    try (OutputStreamWriter writer = new OutputStreamWriter(entityStream)) {
      YamlUtils.writeValue(writer, t);
    }
  }
}
