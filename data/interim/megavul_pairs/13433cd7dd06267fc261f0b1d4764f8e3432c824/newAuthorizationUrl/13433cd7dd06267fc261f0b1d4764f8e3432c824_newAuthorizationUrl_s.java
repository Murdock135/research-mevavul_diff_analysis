class newAuthorizationUrl {
public AuthorizationCodeRequestUrl newAuthorizationUrl() {
    return new AuthorizationCodeRequestUrl(authorizationServerEncodedUrl, clientId).setScopes(
        scopes);
  }
}
