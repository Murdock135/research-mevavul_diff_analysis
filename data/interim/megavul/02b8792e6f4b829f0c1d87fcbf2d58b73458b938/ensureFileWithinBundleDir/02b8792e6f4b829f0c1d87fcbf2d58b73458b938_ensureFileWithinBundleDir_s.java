class ensureFileWithinBundleDir {
private void ensureFileWithinBundleDir(String filename) throws IOException {
        if (!bundleDir.resolve(filename).toFile().getCanonicalPath().startsWith(bundleDir.toFile().getCanonicalPath())) {
            throw new NotFoundException();
        }
    }
}
