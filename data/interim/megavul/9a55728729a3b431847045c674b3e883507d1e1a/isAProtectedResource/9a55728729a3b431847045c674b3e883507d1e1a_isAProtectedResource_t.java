class isAProtectedResource {
public boolean isAProtectedResource(HttpServletRequest request, final boolean onKeywordsOnly) {
    boolean isProtected = false;
    if (request.getRequestURI().matches(UNPROTECTED_URI_RULE)) {
      isProtected = DEFAULT_PROTECTED_METHODS.contains(request.getMethod());
      if (!isProtected && "GET".equals(request.getMethod())) {
        String path = getRequestPath(request);
        isProtected = onKeywordsOnly ?
            path.matches(DEFAULT_GET_RULE_ON_KEYWORD) :
            path.matches(DEFAULT_GET_RULE);
      }
    }
    return isProtected;
  }
}
