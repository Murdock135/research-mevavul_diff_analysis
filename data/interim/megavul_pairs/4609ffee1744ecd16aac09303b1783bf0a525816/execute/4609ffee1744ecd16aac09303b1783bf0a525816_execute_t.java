class execute {
public static void execute(String url, String proxyHost, int proxyPort, String proxyUsername, String proxyPassword,
            String accessToken, String orgName, String moduleName, String version, Path baloPath) {
        HttpsURLConnection conn = createHttpsUrlConnection(convertToUrl(url), proxyHost, proxyPort, proxyUsername,
                                                           proxyPassword);
        conn.setInstanceFollowRedirects(false);
        setRequestMethod(conn, Utils.RequestMethod.POST);

        // Set headers
        conn.setRequestProperty(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        conn.setRequestProperty(PUSH_ORGANIZATION, orgName);
        conn.setRequestProperty(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM);

        conn.setDoOutput(true);
        conn.setChunkedStreamingMode(BUFFER_SIZE);

        try (DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream())) {
            // Send balo content by 1 kb chunks
            byte[] buffer = new byte[BUFFER_SIZE];
            int count;
            try (ProgressBar progressBar = new ProgressBar(
                    orgName + "/" + moduleName + ":" + version + " [project repo -> central]",
                    getTotalFileSizeInKB(baloPath), 1000, outStream, ProgressBarStyle.ASCII, " KB", 1);
                    FileInputStream fis = new FileInputStream(baloPath.toFile())) {
                while ((count = fis.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, count);
                    outputStream.flush();
                    progressBar.stepBy((long) NO_OF_BYTES);
                }
            }
        } catch (IOException e) {
            throw ErrorUtil.createCommandException("error occurred while uploading balo to central: " + e.getMessage());
        }

        handleResponse(conn, orgName, moduleName, version);
        Authenticator.setDefault(null);
    }
}
