class getStatusCode {
public static int getStatusCode(HttpsURLConnection conn) {
        try {
            return conn.getResponseCode();
        } catch (IOException e) {
            throw ErrorUtil
                    .createCommandException("connection to the remote repository host failed: " + e.getMessage());
        }
    }
}
