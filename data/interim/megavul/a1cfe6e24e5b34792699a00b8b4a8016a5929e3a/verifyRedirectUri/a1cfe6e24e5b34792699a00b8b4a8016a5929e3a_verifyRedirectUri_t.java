class verifyRedirectUri {
public static String verifyRedirectUri(KeycloakSession session, String rootUrl, String redirectUri, Set<String> validRedirects, boolean requireRedirectUri) {
        KeycloakUriInfo uriInfo = session.getContext().getUri();
        RealmModel realm = session.getContext().getRealm();

        if (redirectUri == null) {
            if (!requireRedirectUri) {
                redirectUri = getSingleValidRedirectUri(validRedirects);
            }

            if (redirectUri == null) {
                logger.debug("No Redirect URI parameter specified");
                return null;
            }
        } else if (validRedirects.isEmpty()) {
            logger.debug("No Redirect URIs supplied");
            redirectUri = null;
        } else {
            // Make the validations against fully decoded and normalized redirect-url. This also allows wildcards (case when client configured "Valid redirect-urls" contain wildcards)
            String decodedRedirectUri = decodeRedirectUri(redirectUri);
            URI decodedRedirect = toUri(decodedRedirectUri);
            decodedRedirectUri = getNormalizedRedirectUri(decodedRedirect);
            if (decodedRedirectUri == null) return null;

            String r = decodedRedirectUri;
            Set<String> resolveValidRedirects = resolveValidRedirects(session, rootUrl, validRedirects);

            String valid = matchesRedirects(resolveValidRedirects, r, true);

            if (valid == null && (r.startsWith(Constants.INSTALLED_APP_URL) || r.startsWith(Constants.INSTALLED_APP_LOOPBACK)) && r.indexOf(':', Constants.INSTALLED_APP_URL.length()) >= 0) {
                int i = r.indexOf(':', Constants.INSTALLED_APP_URL.length());

                StringBuilder sb = new StringBuilder();
                sb.append(r.substring(0, i));

                i = r.indexOf('/', i);
                if (i >= 0) {
                    sb.append(r.substring(i));
                }

                r = sb.toString();

                valid = matchesRedirects(resolveValidRedirects, r, true);
            }

            // Return the original redirectUri, which can be partially encoded - for example http://localhost:8280/foo/bar%20bar%2092%2F72/3 . Just make sure it is normalized
            URI redirect = toUri(redirectUri);
            redirectUri = getNormalizedRedirectUri(redirect);

            // We try to check validity also for original (encoded) redirectUrl, but just in case it exactly matches some "Valid Redirect URL" specified for client (not wildcards allowed)
            if (valid == null) {
                valid = matchesRedirects(resolveValidRedirects, redirectUri, false);
            }

            if (valid != null && redirectUri.startsWith("/")) {
                redirectUri = relativeToAbsoluteURI(session, rootUrl, redirectUri);
            }

            String scheme = decodedRedirect.getScheme();
            if (valid != null && scheme != null) {
                // check the scheme is valid, it should be http(s) or explicitly allowed by the validation
                if (!valid.startsWith(scheme + ":") && !"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
                    logger.debugf("Invalid URI because scheme is not allowed: %s", redirectUri);
                    valid = null;
                }
            }

            redirectUri = valid != null ? redirectUri : null;
        }

        if (Constants.INSTALLED_APP_URN.equals(redirectUri)) {
            return Urls.realmInstalledAppUrnCallback(uriInfo.getBaseUri(), realm.getName()).toString();
        } else {
            return redirectUri;
        }
    }
}
