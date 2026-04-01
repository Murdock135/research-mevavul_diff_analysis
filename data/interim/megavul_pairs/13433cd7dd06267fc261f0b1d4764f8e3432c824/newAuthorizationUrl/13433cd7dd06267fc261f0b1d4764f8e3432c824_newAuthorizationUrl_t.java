class newAuthorizationUrl {
public AuthorizationCodeRequestUrl newAuthorizationUrl() {
    AuthorizationCodeRequestUrl url = new  AuthorizationCodeRequestUrl(authorizationServerEncodedUrl, clientId);
    url.setScopes(scopes);
    if (pkce != null) {
      url.setCodeChallenge(pkce.getChallenge());
      url.setCodeChallengeMethod(pkce.getChallengeMethod());
    }
    return url;
  }
}
