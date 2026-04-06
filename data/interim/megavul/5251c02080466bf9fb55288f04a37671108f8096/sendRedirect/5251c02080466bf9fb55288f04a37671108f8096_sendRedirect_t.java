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

        // check for trusted domains, only if the given location is an absolute URL.
        if (ABSOLUTE_URL_PATTERN.matcher(redirect).matches()) {
            if (!getURLSecurityManager().isDomainTrusted(new URL(redirect))) {
                LOGGER.warn(
                    "Possible phishing attack, attempting to redirect to [{}], this request has been blocked. "
                        + "If the request was legitimate, add the domain related to this request in the list "
                        + "of trusted domains in the configuration.", redirect);
                return;
            }
        }
        this.response.sendRedirect(redirect);
    }
}
