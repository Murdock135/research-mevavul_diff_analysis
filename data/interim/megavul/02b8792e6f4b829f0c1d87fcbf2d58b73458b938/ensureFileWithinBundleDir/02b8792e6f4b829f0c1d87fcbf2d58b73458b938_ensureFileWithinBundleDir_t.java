class ensureFileWithinBundleDir {
@VisibleForTesting
    void ensureFileWithinBundleDir(Path bundleDir, String filename) {
        if (!bundleDir.resolve(filename).toAbsolutePath().normalize().startsWith(bundleDir.toAbsolutePath().normalize())) {
            throw new NotFoundException();
        }
    }
}
