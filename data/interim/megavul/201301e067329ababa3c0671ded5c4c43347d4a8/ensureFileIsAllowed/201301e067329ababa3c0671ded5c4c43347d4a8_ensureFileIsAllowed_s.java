class ensureFileIsAllowed {
private java.nio.file.Path ensureFileIsAllowed(String fileName) {
        final java.nio.file.Path etcFolderNormalized = etcFolder.normalize();
        final java.nio.file.Path fileNormalized = etcFolder.resolve(fileName).normalize();
        if (!(fileNormalized.getNameCount() > etcFolderNormalized.getNameCount() && fileNormalized.startsWith(etcFolderNormalized))) {
            throw new BadRequestException("Cannot access files outside of folder! Filename given: " + fileName);
        }
        if (!SUPPORTED_FILE_EXTENSIONS.contains(FilenameUtils.getExtension(fileNormalized.getFileName().toString()))) {
            throw new BadRequestException("Unsupported file extension: " + fileName);
        }
        return fileNormalized;
    }
}
