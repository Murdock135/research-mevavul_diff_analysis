class newTokenRequest {
public AuthorizationCodeTokenRequest newTokenRequest(String authorizationCode) {
    HttpExecuteInterceptor pkceClientAuthenticationWrapper = new HttpExecuteInterceptor() {
      @Override
      public void intercept(HttpRequest request) throws IOException {
        clientAuthentication.intercept(request);
        if (pkce != null) {
          Map<String, Object> data = Data.mapOf(UrlEncodedContent.getContent(request).getData());
          data.put("code_verifier", pkce.getVerifier());
        }
      }
    };

    return new AuthorizationCodeTokenRequest(transport, jsonFactory,
        new GenericUrl(tokenServerEncodedUrl), authorizationCode).setClientAuthentication(
        pkceClientAuthenticationWrapper).setRequestInitializer(requestInitializer).setScopes(scopes);
  }
}
