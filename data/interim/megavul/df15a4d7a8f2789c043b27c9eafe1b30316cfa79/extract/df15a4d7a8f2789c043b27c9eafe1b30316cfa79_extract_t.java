class extract {
public static void extract(File zipFile, File destDir) throws IOException {
		try (ZipFile zf = new ZipFile(zipFile)) {
			extract(zf, destDir);
		}
	}
}
