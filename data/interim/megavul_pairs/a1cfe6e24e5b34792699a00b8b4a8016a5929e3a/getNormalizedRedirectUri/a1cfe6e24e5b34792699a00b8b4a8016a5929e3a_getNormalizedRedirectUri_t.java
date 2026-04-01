class getNormalizedRedirectUri {
private static String getNormalizedRedirectUri(URI uri) {
        String redirectUri = null;
        if (uri != null) {
            redirectUri = uri.normalize().toString();
            redirectUri = lowerCaseHostname(redirectUri);
        }
        return redirectUri;
    }
}
