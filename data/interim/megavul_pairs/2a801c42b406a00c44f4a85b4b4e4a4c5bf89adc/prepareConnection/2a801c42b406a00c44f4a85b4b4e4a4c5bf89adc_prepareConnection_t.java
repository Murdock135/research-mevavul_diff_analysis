class prepareConnection {
@Override
    public HttpURLConnection prepareConnection(String path, String method)
            throws IOException {
        // path should have been checked at this point for any outside requests
        URL uri = new URL(WEBPACK_HOST + ":" + getPort() + path);
        HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
        connection.setRequestMethod(method);
        connection.setReadTimeout(DEFAULT_TIMEOUT);
        connection.setConnectTimeout(DEFAULT_TIMEOUT);
        return connection;
    }
}
