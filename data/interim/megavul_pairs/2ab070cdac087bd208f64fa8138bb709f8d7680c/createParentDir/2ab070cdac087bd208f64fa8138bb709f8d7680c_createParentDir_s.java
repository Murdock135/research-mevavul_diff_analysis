class createParentDir {
public static void createParentDir(File f, String eMsg) throws IOException {
        File parent = f.getParentFile();
        if (!parent.isDirectory() && !parent.mkdirs()) {
            throw new IOException("Cant create directory " + (eMsg == null ? parent : eMsg));
        }
    }
}
