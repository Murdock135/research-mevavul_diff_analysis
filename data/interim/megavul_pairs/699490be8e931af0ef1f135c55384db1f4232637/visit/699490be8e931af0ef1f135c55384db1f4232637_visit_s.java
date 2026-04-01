class visit {
public static Path visit(File file, String filename, FileVisitor<Path> visitor) 
	throws IOException, IllegalArgumentException {
		if(!StringHelper.containsNonWhitespace(filename)) {
			filename = file.getName();
		}
		
		Path fPath = null;
		if(file.isDirectory()) {
			fPath = file.toPath();
		} else if(filename != null && filename.toLowerCase().endsWith(".zip")) {
			try {
				fPath = FileSystems.newFileSystem(file.toPath(), null).getPath("/");
			} catch (ProviderNotFoundException | ServiceConfigurationError e) {
				throw new IOException("Unreadable file with .zip extension: " + file, e);
			}
		} else {
			fPath = file.toPath();
		}
		if(fPath != null) {
		    Files.walkFileTree(fPath, visitor);
		}
		return fPath;
	}
}
