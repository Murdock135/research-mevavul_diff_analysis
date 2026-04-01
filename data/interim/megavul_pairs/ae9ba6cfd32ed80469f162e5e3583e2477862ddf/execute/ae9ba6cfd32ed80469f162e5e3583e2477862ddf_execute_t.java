class execute {
private <T> T execute(HttpMethod method, Env env, String path, Object request, Class<T> responseType,
                        Object... uriVariables) {

    if (path.startsWith("/")) {
      path = path.substring(1, path.length());
    }

    String uri = uriTemplateHandler.expand(path, uriVariables).getPath();
    Transaction ct = Tracer.newTransaction("AdminAPI", uri);
    ct.addData("Env", env);

    List<ServiceDTO> services = getAdminServices(env, ct);
    HttpHeaders extraHeaders = assembleExtraHeaders(env);

    for (ServiceDTO serviceDTO : services) {
      try {

        T result = doExecute(method, extraHeaders, serviceDTO, path, request, responseType, uriVariables);

        ct.setStatus(Transaction.SUCCESS);
        ct.complete();
        return result;
      } catch (Throwable t) {
        logger.error("Http request failed, uri: {}, method: {}", uri, method, t);
        Tracer.logError(t);
        if (canRetry(t, method)) {
          Tracer.logEvent(TracerEventType.API_RETRY, uri);
        } else {//biz exception rethrow
          ct.setStatus(t);
          ct.complete();
          throw t;
        }
      }
    }

    //all admin server down
    ServiceException e =
        new ServiceException(String.format("Admin servers are unresponsive. meta server address: %s, admin servers: %s",
                portalMetaDomainService.getDomain(env), services));
    ct.setStatus(e);
    ct.complete();
    throw e;
  }
}
