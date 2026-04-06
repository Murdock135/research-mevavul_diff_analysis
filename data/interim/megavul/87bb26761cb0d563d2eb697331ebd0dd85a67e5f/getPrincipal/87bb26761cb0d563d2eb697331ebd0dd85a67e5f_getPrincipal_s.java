class getPrincipal {
@Override
    public Principal getPrincipal(HttpRequest httpRequest) {
        X509Certificate[] certs = (X509Certificate[]) httpRequest.getAttribute(CERTIFICATES_ATTR);

        if (certs == null || certs.length < 1) {
            if (log.isDebugEnabled()) {
                log.debug("no certificate was present to authenticate the client");
            }

            return null;
        }

        // certs is an array of certificates presented by the client
        // with the first one in the array being the certificate of the client
        // itself.
        X509Certificate identityCert = certs[0];

        return createPrincipal(parseUuid(identityCert));
    }
}
