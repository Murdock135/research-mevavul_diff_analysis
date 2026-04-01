class createFile {
private static File createFile() {
        File file = null;
        try {
            file = File.createTempFile("tmp", "tmp");
            FileOutputStream fos = new FileOutputStream(file);
            for (int i = 0; i < 1000; i++) {
                fos.write("hello".getBytes());
            }
            fos.write("1234".getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }
}
