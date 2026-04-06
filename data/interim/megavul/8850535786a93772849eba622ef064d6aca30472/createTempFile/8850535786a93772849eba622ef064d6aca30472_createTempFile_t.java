class createTempFile {
public static File createTempFile() throws IOException {
        final File file = Files.createTempFile("rep", "tmp").toFile();
        // Make sure the file is deleted when JVM is shutdown at last.
        file.deleteOnExit();
        return file;
    }
}
