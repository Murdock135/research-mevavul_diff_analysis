class validate {
@Override
  public void validate(final OidcCredentials credentials, final WebContext context) {
    final AuthorizationCode code = credentials.getCode();
    // if we have a code
    if (code != null) {
      try {
        final String computedCallbackUrl = client.computeFinalCallbackUrl(context);
        // Token request
        final TokenRequest request = createTokenRequest(new AuthorizationCodeGrant(code, new URI(computedCallbackUrl)));
        HTTPRequest tokenHttpRequest = request.toHTTPRequest();
        tokenHttpRequest.setConnectTimeout(configuration.getConnectTimeout());
        tokenHttpRequest.setReadTimeout(configuration.getReadTimeout());

        final HTTPResponse httpResponse = tokenHttpRequest.send();
        logger.debug("Token response: status={}, content={}", httpResponse.getStatusCode(),
            httpResponse.getContent());

        final TokenResponse response = OIDCTokenResponseParser.parse(httpResponse);
        if (response instanceof TokenErrorResponse) {
          throw new TechnicalException("Bad token response, error=" + ((TokenErrorResponse) response).getErrorObject());
        }
        logger.debug("Token response successful");
        final OIDCTokenResponse tokenSuccessResponse = (OIDCTokenResponse) response;

        // save tokens in credentials
        final OIDCTokens oidcTokens = tokenSuccessResponse.getOIDCTokens();
        credentials.setAccessToken(oidcTokens.getAccessToken());
        credentials.setRefreshToken(oidcTokens.getRefreshToken());
        credentials.setIdToken(oidcTokens.getIDToken());

      } catch (final URISyntaxException | IOException | ParseException e) {
        throw new TechnicalException(e);
      }
    }
  }
}
