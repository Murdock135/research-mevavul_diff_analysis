class buildHttpHeaders {
public static Map<String, String> buildHttpHeaders(String url, String appId, String secret) {
    long currentTimeMillis = System.currentTimeMillis();
    String timestamp = String.valueOf(currentTimeMillis);

    String pathWithQuery = url2PathWithQuery(url);
    String signature = signature(timestamp, pathWithQuery, secret);

    Map<String, String> headers = Maps.newHashMap();
    headers.put(HttpHeaders.AUTHORIZATION, String.format(AUTHORIZATION_FORMAT, appId, signature));
    headers.put(HTTP_HEADER_TIMESTAMP, timestamp);
    return headers;
  }
}
