class readFrom {
@Override
    public File readFrom(Class<File> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
        File downloadedFile = Files.createTempFile(PREFIX, SUFFIX).toFile();
        if (HeaderUtil.isContentLengthZero(httpHeaders)) {
            return downloadedFile;
        }

        try (OutputStream output = new BufferedOutputStream(new FileOutputStream(downloadedFile))) {
            entityStream.transferTo(output);
        }

        return downloadedFile;
    }
}
