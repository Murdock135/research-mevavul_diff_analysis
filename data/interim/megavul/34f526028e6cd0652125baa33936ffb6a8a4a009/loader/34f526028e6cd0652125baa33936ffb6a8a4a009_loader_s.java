class loader {
private static Loader loader(final Path basedir, final ClassLoader classloader) {
    if (Files.exists(basedir)) {
      return name -> {
        Path path = basedir.resolve(name).normalize();
        if (Files.exists(path) && path.startsWith(basedir)) {
          try {
            return path.toUri().toURL();
          } catch (MalformedURLException x) {
            // shh
          }
        }
        return classloader.getResource(name);
      };
    }
    return classloader::getResource;
  }
}
