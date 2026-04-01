class visitFile_1 {
@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
	    throws IOException {
			String filename = file.getFileName().toString();
			Path normalizedPath = file.normalize();
			if(!normalizedPath.startsWith(destDir)) {
				throw new IOException("Invalid ZIP");
			}
			
	        if(filename.endsWith(WikiManager.WIKI_PROPERTIES_SUFFIX)) {
	        	String f = convertAlternativeFilename(file.toString());
	        	final Path destFile = Paths.get(wikiDir.toString(), f);
	        	resetAndCopyProperties(file, destFile);
	        } else if (filename.endsWith(WIKI_FILE_SUFFIX)) {
	        	String f = convertAlternativeFilename(file.toString());
	        	final Path destFile = Paths.get(wikiDir.toString(), f);
	        	Files.copy(file, destFile, StandardCopyOption.REPLACE_EXISTING);
			} else if (!filename.contains(WIKI_FILE_SUFFIX + "-")
					&& !filename.contains(WIKI_PROPERTIES_SUFFIX + "-")) {
				final Path destFile = Paths.get(mediaDir.toString(), file.toString());
				Files.copy(file, destFile, StandardCopyOption.REPLACE_EXISTING);
			}
	        return FileVisitResult.CONTINUE;
		}
}
