class newObjectInputStream {
public static ObjectInputStream newObjectInputStream(final ClassLoader classLoader, InputStream in) throws IOException {
        return new ClassLoaderAwareObjectInputStream(classLoader, in);
    }
}
