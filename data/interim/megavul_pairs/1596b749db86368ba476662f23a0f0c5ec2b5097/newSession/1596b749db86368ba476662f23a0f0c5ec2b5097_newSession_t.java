class newSession {
@POST
    @ApiOperation(value = "Create a new session",
                  notes = "This request creates a new session for a user or reactivates an existing session: the equivalent of logging in.",
                  response = SessionResponse.class)
    @NoAuditEvent("dispatches audit events in the method body")
    public Response newSession(@Context ContainerRequestContext requestContext,
                               @ApiParam(name = "Login request", value = "Credentials. The default " +
                                       "implementation requires presence of two properties: 'username' and " +
                                       "'password'. However a plugin may customize which kind of credentials " +
                                       "are accepted and therefore expect different properties.",
                                         required = true)
                               @NotNull JsonNode createRequest) {

        rejectServiceAccount(createRequest);

        final ActorAwareAuthenticationToken authToken;
        try {
            authToken = tokenFactory.forRequestBody(createRequest);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage());
        }

        final String host = RestTools.getRemoteAddrFromRequest(grizzlyRequest, trustedSubnets);

        try {
            // Always create a brand-new session for an authentication attempt by ignoring any previous session ID.
            // This avoids a potential session fixation attack. (GHSA-3xf8-g8gr-g7rh)
            Optional<Session> session = sessionCreator.login(null, host, authToken);
            if (session.isPresent()) {
                final SessionResponse token = sessionResponseFactory.forSession(session.get());
                return Response.ok()
                        .entity(token)
                        .cookie(cookieFactory.createAuthenticationCookie(token, requestContext))
                        .build();
            } else {
                throw new NotAuthorizedException("Invalid credentials.", "Basic realm=\"Graylog Server session\"");
            }
        } catch (AuthenticationServiceUnavailableException e) {
            throw new ServiceUnavailableException("Authentication service unavailable");
        }
    }
}
