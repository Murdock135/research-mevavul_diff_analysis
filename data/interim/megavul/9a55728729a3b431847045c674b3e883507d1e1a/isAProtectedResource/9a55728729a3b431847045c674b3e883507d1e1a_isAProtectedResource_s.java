class isAProtectedResource {
public boolean isAProtectedResource(HttpServletRequest request) {
    boolean isProtected = false;
    if (request.getRequestURI().matches(UNPROTECTED_URI_RULE)) {
      isProtected = DEFAULT_PROTECTED_METHODS.contains(request.getMethod());
      if (!isProtected && request.getMethod().equals("GET")) {
        String path = getRequestPath(request);
        isProtected = path.matches(DEFAULT_GET_RULE);
      }
    }
    return isProtected;
  }
}
