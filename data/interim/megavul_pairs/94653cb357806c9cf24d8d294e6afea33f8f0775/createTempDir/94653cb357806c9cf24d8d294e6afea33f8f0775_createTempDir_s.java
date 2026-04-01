class createTempDir {
public static File createTempDir(int port) throws IOException {
        File tempDir = File.createTempFile("portofino.tomcat.", "." + port);
        tempDir.delete();
        tempDir.mkdir();
        tempDir.deleteOnExit();
        return tempDir;
    }
}
