class expandIfZip {
public static Path expandIfZip(Path filePath) throws IOException {
        if (!isZipFile(filePath)) {
            return filePath;
        }

        FileTime pluginZipDate = Files.getLastModifiedTime(filePath);
        String fileName = filePath.getFileName().toString();
        String directoryName = fileName.substring(0, fileName.lastIndexOf("."));
        Path pluginDirectory = filePath.resolveSibling(directoryName);

        // Check whether directory traversal risks exist in the path
        if (!isInvalidPath(pluginDirectory)) {
            throw new SecurityException("Invalid destination directory");
        }

        if (!Files.exists(pluginDirectory) || pluginZipDate.compareTo(Files.getLastModifiedTime(pluginDirectory)) > 0) {
            // expand '.zip' file
            Unzip unzip = new Unzip();
            unzip.setSource(filePath.toFile());
            unzip.setDestination(pluginDirectory.toFile());
            unzip.extract();
            log.info("Expanded plugin zip '{}' in '{}'", filePath.getFileName(), pluginDirectory.getFileName());
        }

        return pluginDirectory;
    }
}
