class init {
private void init(final String classPathPrefix, final String location, final Path basedir,
      final ClassLoader loader) {
    requireNonNull(loader, "Resource loader is required.");
    this.fn = location.equals("/")
        ? (req, p) -> prefix.apply(p)
        : (req, p) -> MessageFormat.format(prefix.apply(location), vars(req));
    this.loader = loader(basedir, classpathLoader(classPathPrefix, classLoader));
  }
}
