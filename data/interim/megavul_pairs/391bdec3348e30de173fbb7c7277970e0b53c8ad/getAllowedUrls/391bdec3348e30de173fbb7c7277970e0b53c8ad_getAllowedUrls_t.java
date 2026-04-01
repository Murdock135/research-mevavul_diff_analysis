class getAllowedUrls {
public List<String> getAllowedUrls() {
        String allowedURL = prop.getProperty(TS_ALLOWED_URLS, DEFAULT_TS_ALLOWED_URLS);
        return Arrays.asList(allowedURL.split(","));
    }
}
