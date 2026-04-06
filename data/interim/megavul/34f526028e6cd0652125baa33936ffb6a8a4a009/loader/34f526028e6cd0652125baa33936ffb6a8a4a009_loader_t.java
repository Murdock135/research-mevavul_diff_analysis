class loader {
private static Loader loader(final Path basedir, Loader classpath) {
    if (basedir != null && Files.exists(basedir)) {
      return name -> {
        Path path = basedir.resolve(name).normalize();
        if (Files.exists(path) && path.startsWith(basedir)) {
          try {
            return path.toUri().toURL();
          } catch (MalformedURLException x) {
            // shh
          }
        }
        return classpath.getResource(name);
      };
    }
    return classpath;
  }
}
