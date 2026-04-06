class load {
private void load(@NotNull Dependency dependency) throws LoadFailureException {
		try {
			classLoader.addURL(dragonfly.getDirectory().resolve(dependency.getFileName()).toUri().toURL());
		} catch (MalformedURLException ex) {
			throw new LoadFailureException(ex);
		}
	}
}
