class toFileInput {
@Nullable
    private FileInput toFileInput(URI uri, Settings withClauseOptions) {
        FileInputFactory fileInputFactory = fileInputFactories.get(uri.getScheme());
        if (fileInputFactory != null) {
            try {
                return fileInputFactory.create(uri, withClauseOptions);
            } catch (IOException e) {
                return null;
            }
        }
        return new URLFileInput(uri);
    }
}
