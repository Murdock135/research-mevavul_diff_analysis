class classFromName {
@Nullable
    private Class<?> classFromName(String className) {
        try {
            return chainingClassLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
