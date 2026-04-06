class get {
@Override
  public Route.Definition get(final String path, final Route.Handler handler) {
    return appendDefinition(GET, path, handler);
  }
}
