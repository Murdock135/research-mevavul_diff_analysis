class setRequestMethod {
public static void setRequestMethod(HttpURLConnection conn, RequestMethod method) {
        try {
            conn.setRequestMethod(getRequestMethodAsString(method));
        } catch (ProtocolException e) {
            throw ErrorUtil.createCommandException(e.getMessage());
        }
    }
}
