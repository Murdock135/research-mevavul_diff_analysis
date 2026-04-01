class visitFile_1 {
@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
	    throws IOException {
			Path relativeFile = source.relativize(file);
	        final Path destFile = Paths.get(destDir.toString(), relativeFile.toString());
	        Path normalizedPath = destFile.normalize();
			if(!normalizedPath.startsWith(destDir)) {
				throw new OLATRuntimeException("Invalid ZIP");
			}
	        if(filter.matches(file)) {
	        	Files.copy(file, destFile, StandardCopyOption.REPLACE_EXISTING);
	        }
	        return FileVisitResult.CONTINUE;
		}
}
