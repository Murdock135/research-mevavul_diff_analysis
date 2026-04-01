class copyJarEntryTrimmingBasePath {
private void copyJarEntryTrimmingBasePath(JarFile jarFile,
            ZipEntry jarEntry, String basePath, File outputDirectory) {
        String fullPath = jarEntry.getName();
        String relativePath = fullPath
                .substring(fullPath.toLowerCase(Locale.ENGLISH)
                        .indexOf(basePath.toLowerCase(Locale.ENGLISH))
                        + basePath.length());
        File target = new File(outputDirectory, relativePath);
        try {
            if (target.exists()) {
                if (!hasSameContent(jarFile.getInputStream(jarEntry), target)) {
                    FileUtils.copyInputStreamToFile(
                            jarFile.getInputStream(jarEntry), target);
                }
            } else {
                FileUtils.copyInputStreamToFile(
                        jarFile.getInputStream(jarEntry), target);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(String.format(
                    "Failed to extract jar entry '%s' from jarFile '%s'",
                    jarEntry, outputDirectory), e);
        }
    }
}
