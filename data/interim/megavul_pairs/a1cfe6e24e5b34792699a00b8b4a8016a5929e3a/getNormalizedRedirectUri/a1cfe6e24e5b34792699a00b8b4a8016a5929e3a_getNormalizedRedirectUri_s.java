class getNormalizedRedirectUri {
private static String getNormalizedRedirectUri(String redirectUri) {
        if (redirectUri != null) {
            try {
                URI uri = URI.create(redirectUri);
                redirectUri = uri.normalize().toString();
            } catch (IllegalArgumentException cause) {
                logger.debug("Invalid redirect uri", cause);
                return null;
            } catch (Exception cause) {
                logger.debug("Unexpected error when parsing redirect uri", cause);
                return null;
            }
            redirectUri = lowerCaseHostname(redirectUri);
        }
        return redirectUri;
    }
}
