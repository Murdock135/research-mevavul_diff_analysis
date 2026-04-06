class doFilter {
@Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    if (SecuritySettings.isWebSecurityByTokensEnabled() && isProtectedResource(httpRequest)) {
      try {
        checkAuthenticatedRequest(httpRequest);
        tokenService.validate(httpRequest, false);
        chain.doFilter(request, response);
      } catch (TokenValidationException ex) {
        ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN);
      } catch (UnauthenticatedRequestException ex) {
        logger.error("The request for path {0} isn''t sent within an opened session",
            ((HttpServletRequest) request).getRequestURI());
        redirectToAuthenticationPage(request, response);
      }
    } else {
      chain.doFilter(request, response);
    }
  }
}
