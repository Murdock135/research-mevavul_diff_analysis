class createSmallFile {
private static File createSmallFile() {
        java.nio.file.Path smallfile = null;
        try {
            smallfile = Files.createTempFile("smalltmp", "tmp");
            try (BufferedWriter writer = Files.newBufferedWriter(smallfile)) {
                writer.write("123456789");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return smallfile.toFile();
    }
}
