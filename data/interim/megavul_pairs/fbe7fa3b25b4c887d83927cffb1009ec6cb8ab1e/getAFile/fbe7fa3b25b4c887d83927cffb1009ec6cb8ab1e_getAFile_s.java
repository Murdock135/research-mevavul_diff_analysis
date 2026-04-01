class getAFile {
public AFile getAFile(String nameOrPath) throws IOException {
		// Log.info("ImportedFiles::getAFile nameOrPath = " + nameOrPath);
		// Log.info("ImportedFiles::getAFile currentDir = " + currentDir);
		final AParentFolder dir = currentDir;
		if (dir == null || isAbsolute(nameOrPath)) {
			return new AFileRegular(new SFile(nameOrPath).getCanonicalFile());
		}
		// final File filecurrent = SecurityUtils.File(dir.getAbsoluteFile(),
		// nameOrPath);
		final AFile filecurrent = dir.getAFile(nameOrPath);
		Log.info("ImportedFiles::getAFile filecurrent = " + filecurrent);
		if (filecurrent != null && filecurrent.isOk()) {
			return filecurrent;
		}
		for (SFile d : getPath()) {
			if (d.isDirectory()) {
				final SFile file = d.file(nameOrPath);
				if (file.exists()) {
					return new AFileRegular(file.getCanonicalFile());
				}
			} else if (d.isFile()) {
				final AFileZipEntry zipEntry = new AFileZipEntry(d, nameOrPath);
				if (zipEntry.isOk()) {
					return zipEntry;
				}
			}
		}
		return filecurrent;
	}
}
