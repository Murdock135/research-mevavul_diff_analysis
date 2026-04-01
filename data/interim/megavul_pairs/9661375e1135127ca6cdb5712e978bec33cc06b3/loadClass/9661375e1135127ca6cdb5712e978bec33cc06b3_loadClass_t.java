class loadClass {
@Override
	protected @NotNull Class<?> loadClass(@NotNull String name, boolean resolve) throws ClassNotFoundException {
		Class<?> loadedClass = findLoadedClass(name);
		if (null == loadedClass) {
			try {
				loadedClass = findClass(name);
			} catch (ClassNotFoundException ex) {
				loadedClass = super.loadClass(name, resolve);
			}
		}

		if (resolve) {
			resolveClass(loadedClass);
		}

		return loadedClass;
	}
}
