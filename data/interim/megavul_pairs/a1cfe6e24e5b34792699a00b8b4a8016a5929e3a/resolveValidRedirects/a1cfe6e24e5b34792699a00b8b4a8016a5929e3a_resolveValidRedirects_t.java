class resolveValidRedirects {
public static Set<String> resolveValidRedirects(KeycloakSession session, String rootUrl, Set<String> validRedirects) {
        // If the valid redirect URI is relative (no scheme, host, port) then use the request's scheme, host, and port
        // the set is ordered by length to get the longest match first
        Set<String> resolveValidRedirects = new TreeSet<>((String s1, String s2) -> s1.length() == s2.length()? s1.compareTo(s2) : s1.length() < s2.length()? 1 : -1);
        for (String validRedirect : validRedirects) {
            if (validRedirect.startsWith("/")) {
                validRedirect = relativeToAbsoluteURI(session, rootUrl, validRedirect);
                logger.debugv("replacing relative valid redirect with: {0}", validRedirect);
                resolveValidRedirects.add(validRedirect);
            } else {
                resolveValidRedirects.add(validRedirect);
            }
        }
        return resolveValidRedirects;
    }
}
