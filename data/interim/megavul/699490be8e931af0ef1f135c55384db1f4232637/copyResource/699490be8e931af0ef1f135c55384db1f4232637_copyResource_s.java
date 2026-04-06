class copyResource {
public static boolean copyResource(File file, String filename, File targetDirectory, PathMatcher filter) {
		try {
			Path path = getResource(file, filename);
			if(path == null) {
				return false;
			}
			
			Path destDir = targetDirectory.toPath();
			Files.walkFileTree(path, new CopyVisitor(path, destDir, filter));
			PathUtils.closeSubsequentFS(path);
			return true;
		} catch (IOException e) {
			log.error("", e);
			return false;
		}
	}
}
