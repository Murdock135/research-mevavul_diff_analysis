class downloadBundle {
public void downloadBundle(String filename, OutputStream outputStream) throws IOException {
        ensureFileWithinBundleDir(bundleDir, filename);

        try {
            final Path filePath = bundleDir.resolve(filename);
            Files.copy(filePath, outputStream);
        } catch (NoSuchFileException e) {
            throw new NotFoundException(e);
        } catch (Exception e) {
            outputStream.close();
        }
    }
}
