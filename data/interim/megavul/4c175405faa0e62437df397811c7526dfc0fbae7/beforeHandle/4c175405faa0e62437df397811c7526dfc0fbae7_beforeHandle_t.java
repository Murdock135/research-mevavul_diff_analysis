class beforeHandle {
@Override
    protected int beforeHandle(Request request, Response response)
    {
        ComponentManager componentManager =
            (ComponentManager) getContext().getAttributes().get(Constants.XWIKI_COMPONENT_MANAGER);
        XWikiContext xwikiContext = Utils.getXWikiContext(componentManager);
        CSRFToken csrfToken = null;

        try {
            csrfToken = componentManager.getInstance(CSRFToken.class);
        } catch (ComponentLookupException e) {
            getLogger().warning("Failed to lookup CSRF token validator: " + ExceptionUtils.getRootCauseMessage(e));
        }

        try {
            EntityReferenceSerializer<String> serializer =
                componentManager.getInstance(EntityReferenceSerializer.TYPE_STRING);

            /*
             * We add headers to the response to allow applications to verify if the authentication is still valid. We
             * are also adding the XWiki version at the same moment.
             */
            Series<Header> responseHeaders =
                (Series<Header>) response.getAttributes().get(HeaderConstants.ATTRIBUTE_HEADERS);
            if (responseHeaders == null) {
                responseHeaders = new Series<>(Header.class);
                response.getAttributes().put(HeaderConstants.ATTRIBUTE_HEADERS, responseHeaders);
            }
            responseHeaders.add("XWiki-User", serializer.serialize(xwikiContext.getUserReference()));
            responseHeaders.add("XWiki-Version", xwikiContext.getWiki().getVersion());

            if (csrfToken != null) {
                responseHeaders.add(FORM_TOKEN_HEADER, csrfToken.getToken());
            }
        } catch (ComponentLookupException e) {
            getLogger()
                .warning("Failed to lookup the entity reference serializer: " + ExceptionUtils.getRootCauseMessage(e));
        }

        int result = CONTINUE;

        HttpServletRequest servletRequest = ServletUtils.getRequest(Request.getCurrent());

        // Require a CSRF token for requests that browsers allow through HTML forms and across origins.
        // See https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS for more information.
        // Compare to the method from the servlet request to avoid the automatic conversion from POST to PUT request.
        // Check for a prefix match to make sure it matches regardless of the supplied parameters (like charset).
        if ("POST".equals(servletRequest.getMethod()) && SIMPLE_CONTENT_TYPES.stream().anyMatch(expectedType ->
            StringUtils.startsWith(StringUtils.lowerCase(servletRequest.getContentType()), expectedType)))
        {
            Series<Header> requestHeaders = request.getHeaders();
            String formToken = requestHeaders.getFirstValue(FORM_TOKEN_HEADER);

            // Skip the main request handler but allow cleanup if either the CSRF validator failed or the token is
            // invalid.
            if (csrfToken == null) {
                response.setStatus(Status.SERVER_ERROR_INTERNAL);
                response.setEntity("Failed to lookup the CSRF token validator.", MediaType.TEXT_PLAIN);
                result = SKIP;
            } else if (!csrfToken.isTokenValid(formToken)) {
                response.setStatus(Status.CLIENT_ERROR_FORBIDDEN);
                response.setEntity("Invalid or missing form token.", MediaType.TEXT_PLAIN);
                result = SKIP;
            }
        }

        return result;
    }
}
