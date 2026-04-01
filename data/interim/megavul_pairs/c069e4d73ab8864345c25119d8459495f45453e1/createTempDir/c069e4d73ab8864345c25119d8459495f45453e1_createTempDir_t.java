class createTempDir {
public static File createTempDir() throws IOException {
		return Files.createTempDirectory("okm").toFile();
	}
}
