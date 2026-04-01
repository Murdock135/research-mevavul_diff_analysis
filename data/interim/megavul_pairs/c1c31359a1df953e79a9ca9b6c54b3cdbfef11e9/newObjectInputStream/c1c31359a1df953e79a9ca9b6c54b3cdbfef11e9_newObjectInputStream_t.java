class newObjectInputStream {
public static ObjectInputStream newObjectInputStream(final ClassLoader classLoader, ClassNameFilter classFilter,
            InputStream in) throws IOException {
        return new ClassLoaderAwareObjectInputStream(classLoader, classFilter, in);
    }
}
