class get {
@Override
  public Route.Definition get(final String path, final Route.Handler handler) {
    if (handler instanceof AssetHandler) {
      return assets(path, (AssetHandler) handler);
    } else {
      return appendDefinition(GET, path, handler);
    }
  }
}
