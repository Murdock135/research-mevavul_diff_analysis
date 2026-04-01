class createFile {
private static File createFile() {
        java.nio.file.Path file = null;
        try {
            file = Files.createTempFile("tmp", "tmp");
            try (BufferedWriter writer = Files.newBufferedWriter(file)) {
                for (int i = 0; i < 1000; i++) {
                    writer.write("hello");
                }
                writer.write("1234");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file.toFile();
    }
}
