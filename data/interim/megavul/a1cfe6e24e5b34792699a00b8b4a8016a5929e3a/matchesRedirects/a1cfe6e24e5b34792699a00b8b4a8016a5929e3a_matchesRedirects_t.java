class matchesRedirects {
private static String matchesRedirects(Set<String> validRedirects, String redirect, boolean allowWildcards) {
        logger.tracef("matchesRedirects: redirect URL to check: %s, allow wildcards: %b, Configured valid redirect URLs: %s", redirect, allowWildcards, validRedirects);
        for (String validRedirect : validRedirects) {
            if (validRedirect.endsWith("*") && !validRedirect.contains("?") && allowWildcards) {
                // strip off the query component - we don't check them when wildcards are effective
                String r = redirect.contains("?") ? redirect.substring(0, redirect.indexOf("?")) : redirect;
                // strip off *
                int length = validRedirect.length() - 1;
                validRedirect = validRedirect.substring(0, length);
                if (r.startsWith(validRedirect)) return validRedirect;
                // strip off trailing '/'
                if (length - 1 > 0 && validRedirect.charAt(length - 1) == '/') length--;
                validRedirect = validRedirect.substring(0, length);
                if (validRedirect.equals(r)) return validRedirect;
            } else if (validRedirect.equals(redirect)) return validRedirect;
        }
        return null;
    }
}
