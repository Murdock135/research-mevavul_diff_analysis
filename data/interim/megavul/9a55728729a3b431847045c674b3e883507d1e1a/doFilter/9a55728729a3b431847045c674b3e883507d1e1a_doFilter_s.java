class doFilter {
@Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    if (SecuritySettings.isWebSecurityByTokensEnabled() && isProtectedResource(httpRequest)) {
      try {
        checkAuthenticatedRequest(httpRequest);
        tokenService.validate(httpRequest);
        chain.doFilter(request, response);
      } catch (TokenValidationException ex) {
        logger.error("The request for path {0} isn''t valid: {1}", pathOf(request), ex.getMessage());
        ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN);
      } catch (UnauthenticatedRequestException ex) {
        logger.error("The request for path {0} isn''t sent within an opened session",
            pathOf(request));
        redirectToAuthenticationPage(request, response);
      }
    } else {
      chain.doFilter(request, response);
    }
  }
}
