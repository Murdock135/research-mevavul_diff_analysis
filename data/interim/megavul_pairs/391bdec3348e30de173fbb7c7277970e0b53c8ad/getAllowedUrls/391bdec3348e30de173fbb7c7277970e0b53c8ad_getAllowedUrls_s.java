class getAllowedUrls {
public List<String> getAllowedUrls() {
        String allowedURL = prop.getProperty(TS_ALLOWED_URLS, "file://.*|http(s)?://.*");
        return Arrays.asList(allowedURL.split(","));
    }
}
