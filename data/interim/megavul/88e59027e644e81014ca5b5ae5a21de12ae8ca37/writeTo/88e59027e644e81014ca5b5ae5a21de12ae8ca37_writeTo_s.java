class writeTo {
@Override
  public void writeTo(T t, Class<?> type, Type genericType,
      Annotation[] annotations,
      MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
      OutputStream entityStream) throws IOException, WebApplicationException {
    Yaml yaml = new Yaml();
    OutputStreamWriter writer = new OutputStreamWriter(entityStream);
    yaml.dump(t, writer);
    writer.close();
  }
}
