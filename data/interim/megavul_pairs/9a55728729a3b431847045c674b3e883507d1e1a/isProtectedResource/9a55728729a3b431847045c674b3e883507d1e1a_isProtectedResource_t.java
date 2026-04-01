class isProtectedResource {
private boolean isProtectedResource(HttpServletRequest request) {
    return tokenService.isAProtectedResource(request, false) && !isFileDragAndDrop(request) &&
        !isCredentialManagement(request) && !isSsoAuthentication(request) &&
        !isWebServiceRequested(request) &&
        !isWebBrowserEditionResource(request) && !isCMISResource(request) &&
        !isDragAndDropWebEditionResource(request);
  }
}
