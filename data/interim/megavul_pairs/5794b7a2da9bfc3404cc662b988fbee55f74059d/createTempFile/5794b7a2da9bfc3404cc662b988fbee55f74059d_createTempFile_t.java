class createTempFile {
public static File createTempFile() throws IOException {
        final List<IOException> exs = new ArrayList<>();
        final File file = AccessController.doPrivileged(new PrivilegedAction<File>() {
            public File run() {
                File tempFile = null;
                try {
                    tempFile = Files.createTempFile("rep", "tmp").toFile();
                    // Make sure the file is deleted when JVM is shutdown at last.
                    tempFile.deleteOnExit();
                } catch (IOException e) {
                    exs.add(e);
                }
                return tempFile;
            }
        });
        if (!exs.isEmpty()) {
            throw exs.get(0);
        }
        return file;
    }
}
