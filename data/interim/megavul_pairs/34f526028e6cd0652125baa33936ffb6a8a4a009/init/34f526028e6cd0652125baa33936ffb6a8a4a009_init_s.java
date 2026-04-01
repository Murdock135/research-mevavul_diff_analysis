class init {
private void init(final String pattern, final Path basedir, final ClassLoader loader) {
    requireNonNull(loader, "Resource loader is required.");
    this.fn = pattern.equals("/")
        ? (req, p) -> prefix.apply(p)
        : (req, p) -> MessageFormat.format(prefix.apply(pattern), vars(req));
    this.loader = loader(basedir, loader);
  }
}
