class invoke {
@Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        String characterEncoding = getCharacterEncoding();
        if (characterEncoding != null) {
            request.setCharacterEncoding(characterEncoding);
        }

        // Look for unauthorized status
        if (isUnauthorized(response)) {
            handleUnauthorizedResponse(request, response);
            return;
        }

        // first, we populate all required parameters sent into session for later retrieval. If they exists.
        populateSessionWithSAMLParameters(request);

        // get an authenticated user or tries to authenticate if this is a authentication request
        Principal userPrincipal = request.getPrincipal();

        if (userPrincipal == null) {
            if (getIdpConfiguration().isSSLClientAuthentication()) {
                if (request.isSecure()) {
                    getSSLAuthenticator().invoke(request, response);

                    // we always reset/recycle the response to remove any data written to the response by the ssl
                    // authenticator
                    response.resetBuffer();
                    response.recycle();
                }
            }
        }

        HttpSession session = request.getSession();

        if (isAjaxRequest(request) && session.getAttribute(IDP_SESSION_USER) == null) {
            response.sendError(403);
            return;
        }

        invokeNextValve(request, response);

        userPrincipal = request.getUserPrincipal();

        // we only handle SAML messages for authenticated users.
        if (userPrincipal != null) {
            if (session.getAttribute(IDP_SESSION_USER) == null) {
                session.setAttribute(IDP_SESSION_USER, userPrincipal);
            }

            if (isGlobalLogout(request) && request.getParameter(SAML_REQUEST_KEY) == null) {
                prepareLocalGlobalLogoutRequest(request, userPrincipal);
            }

            handleSAMLMessage(request, response);
        }
    }
}
