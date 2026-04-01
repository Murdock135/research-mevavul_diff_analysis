class visitFile {
@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
    throws IOException {
		Path relativeFile = source.relativize(file);
		final Path destFile = Paths.get(destDir.toString(), relativeFile.toString());
		if(filter.matches(file)) {
			String filename = file.getFileName().toString();
			if(filename.startsWith(".")) {
				//ignore
			} else if(filename.endsWith("xml") && !filename.equals("imsmanifest.xml")) {
				checkPath(destFile);
				convertXmlFile(file, destFile);
			} else {
				checkPath(destFile);
				Files.copy(file, destFile, StandardCopyOption.REPLACE_EXISTING);
			}
		}
        return FileVisitResult.CONTINUE;
	}
}
