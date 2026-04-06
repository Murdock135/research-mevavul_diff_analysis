class getFile {
public FileWithSuffix getFile(String filename, String suffix) throws IOException {
		final int idx = filename.indexOf('~');
		final AFile file;
		final String entry;
		if (idx == -1) {
			file = getAFile(filename);
			entry = null;
		} else {
			file = getAFile(filename.substring(0, idx));
			entry = filename.substring(idx + 1);
		}
		// if (isAllowed(file) == false)
		if (file == null || file.getUnderlyingFile().isFileOk() == false)
			return FileWithSuffix.none();

		return new FileWithSuffix(filename, suffix, file, entry);
	}
}
