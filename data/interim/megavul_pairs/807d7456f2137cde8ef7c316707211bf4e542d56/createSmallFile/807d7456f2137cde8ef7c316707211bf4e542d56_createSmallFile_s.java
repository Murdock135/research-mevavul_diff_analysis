class createSmallFile {
private static File createSmallFile() {
        File smallfile = null;
        try {
            smallfile = File.createTempFile("smalltmp", "tmp");
            FileOutputStream fos = new FileOutputStream(smallfile);
            fos.write("123456789".getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return smallfile;
    }
}
