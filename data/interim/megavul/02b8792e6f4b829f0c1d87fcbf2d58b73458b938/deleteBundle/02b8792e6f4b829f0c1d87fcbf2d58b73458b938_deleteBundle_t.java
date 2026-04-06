class deleteBundle {
public void deleteBundle(String filename) throws IOException {
        ensureFileWithinBundleDir(bundleDir, filename);
        final Path filePath = bundleDir.resolve(filename);
        Files.delete(filePath);
    }
}
