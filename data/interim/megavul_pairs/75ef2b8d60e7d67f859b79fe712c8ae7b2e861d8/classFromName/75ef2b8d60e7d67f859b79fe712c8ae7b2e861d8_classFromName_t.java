class classFromName {
@Nullable
    private Class<?> classFromName(String className) {
        try {
            return chainingClassLoader.loadClassSafely(className);
        } catch (ClassNotFoundException e) {
            return null;
        } catch (UnsafeClassLoadingAttemptException e) {
            throw new BadRequestException(e.getLocalizedMessage());
        }
    }
}
