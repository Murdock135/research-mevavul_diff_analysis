class isProtectedResource {
private boolean isProtectedResource(HttpServletRequest request) {
    return tokenService.isAProtectedResource(request) && !isFileDragAndDrop(request) &&
        !isCredentialManagement(request) && !isSsoAuthentication(request) &&
        !isWebServiceRequested(request) &&
        !isWebBrowserEditionResource(request) && !isCMISResource(request) &&
        !isDragAndDropWebEditionResource(request);
  }
}
