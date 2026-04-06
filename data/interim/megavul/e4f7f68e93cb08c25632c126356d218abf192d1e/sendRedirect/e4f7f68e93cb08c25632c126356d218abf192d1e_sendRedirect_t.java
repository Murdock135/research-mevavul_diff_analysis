class sendRedirect {
@Override
    public void sendRedirect(String redirect) throws IOException
    {
        if (StringUtils.isBlank(redirect)) {
            // Nowhere to go to
            return;
        }
        if (StringUtils.containsAny(redirect, '\r', '\n')) {
            LOGGER.warn("Possible HTTP Response Splitting attack, attempting to redirect to [{}]", redirect);
            return;
        }

        if (StringUtils.startsWith(redirect, "//")) {
            LOGGER.warn("Possible phishing attack, attempting to redirect to [{}]. If this request is legitimate, "
                + "use an actual absolute URL and pay attention to configure properly url.trustedDomains in "
                + "xwiki.properties", redirect);
            return;
        }

        // check for trusted domains, only if the given location is an absolute URL.
        if (ABSOLUTE_URL_PATTERN.matcher(redirect).matches()) {
            if (!getURLSecurityManager().isDomainTrusted(new URL(redirect))) {
                LOGGER.warn(
                    "Possible phishing attack, attempting to redirect to [{}], this request has been blocked. "
                        + "If the request was legitimate, add the domain related to this request in the list "
                        + "of trusted domains in the configuration: it can be configured in xwiki.properties in "
                        + "url.trustedDomains.", redirect);
                return;
            }
        }
        this.response.sendRedirect(redirect);
    }
}
