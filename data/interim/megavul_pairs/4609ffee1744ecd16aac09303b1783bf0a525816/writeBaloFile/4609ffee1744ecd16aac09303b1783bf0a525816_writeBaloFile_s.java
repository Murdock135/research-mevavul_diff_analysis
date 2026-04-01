class writeBaloFile {
private static void writeBaloFile(HttpURLConnection conn, Path baloPath, String fullModuleName,
            long resContentLength) {
        try (InputStream inputStream = conn.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(baloPath.toString())) {
            writeAndHandleProgress(inputStream, outputStream, resContentLength / 1024, fullModuleName);
        } catch (IOException e) {
            createError("error occurred copying the balo file: " + e.getMessage());
        }
    }
}
