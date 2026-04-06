class closeSubsequentFS {
public static void closeSubsequentFS(Path path) {
		if(path != null && FileSystems.getDefault() != path.getFileSystem()) {
			IOUtils.closeQuietly(path.getFileSystem(), null);
		}
	}
}
