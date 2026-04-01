class execute_2 {
public static void execute(String url, String proxyHost, int proxyPort, String proxyUsername, String proxyPassword,
            String terminalWidth) {
        initializeSsl();
        HttpURLConnection conn = createHttpUrlConnection(convertToUrl(url), proxyHost, proxyPort, proxyUsername,
                proxyPassword);
        conn.setInstanceFollowRedirects(false);
        setRequestMethod(conn, Utils.RequestMethod.GET);
        handleResponse(conn, getStatusCode(conn), terminalWidth);
        Authenticator.setDefault(null);
    }
}
