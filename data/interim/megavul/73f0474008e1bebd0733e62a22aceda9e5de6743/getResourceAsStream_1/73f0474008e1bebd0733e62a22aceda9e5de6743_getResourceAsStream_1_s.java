class getResourceAsStream_1 {
@Override
    public InputStream getResourceAsStream(String path) {
        return classLoader.getResourceAsStream(resourceRoot + path);
    }
}
