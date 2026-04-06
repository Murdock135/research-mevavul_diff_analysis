class getFile {
private File getFile(String... path) {
        File result = baseDir;
        for (String p : path) {
            result = new File(result, p);
        }
        return result;
    }
}
