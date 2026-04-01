class getResourceAsStream_1 {
@Override
    public InputStream getResourceAsStream(String path) throws IOException {
        final URL rootResourceURL = classLoader.getResource(resourceRoot);
        if (rootResourceURL == null) {
            return null;
        }
        final String rootPath = rootResourceURL.getPath();
        final URL resourceURL = classLoader.getResource(resourceRoot + path);
        if(resourceURL == null || !resourceURL.getPath().startsWith(rootPath)) {
            return null;
        }
        else {
            return resourceURL.openConnection().getInputStream();
        }
    }
}
