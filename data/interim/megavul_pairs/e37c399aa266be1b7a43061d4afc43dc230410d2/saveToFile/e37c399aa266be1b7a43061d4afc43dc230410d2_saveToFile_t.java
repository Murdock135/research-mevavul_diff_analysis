class saveToFile {
public static void saveToFile(String content, Path outFile) throws IOException {
		if (!Files.exists(outFile.getParent())) {
			Files.createDirectories(outFile.getParent());
		}
		try (Writer writer = Files.newBufferedWriter(outFile, StandardCharsets.UTF_8)) {
			writer.write(content);
		}
		// Make sure it's not executable
		try {
			Files.setPosixFilePermissions(outFile, PosixFilePermissions.fromString("rw-r-----"));
		} catch (UnsupportedOperationException e) {
			// The associated file system does not support the PosixFileAttributeView,
			// ignore the error
			LOGGER.log(Level.SEVERE,
					"The associated file system '" + outFile + "' + does not support the PosixFileAttributeView", e);
		}
	}
}
