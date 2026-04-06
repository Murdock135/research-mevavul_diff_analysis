class newTokenRequest {
public AuthorizationCodeTokenRequest newTokenRequest(String authorizationCode) {
    return new AuthorizationCodeTokenRequest(transport, jsonFactory,
        new GenericUrl(tokenServerEncodedUrl), authorizationCode).setClientAuthentication(
        clientAuthentication).setRequestInitializer(requestInitializer).setScopes(scopes);
  }
}
