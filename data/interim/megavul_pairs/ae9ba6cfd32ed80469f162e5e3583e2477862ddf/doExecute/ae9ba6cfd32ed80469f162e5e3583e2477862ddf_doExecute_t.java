class doExecute {
private <T> T doExecute(HttpMethod method, HttpHeaders extraHeaders, ServiceDTO service, String path, Object request,
                          Class<T> responseType, Object... uriVariables) {
    T result = null;
    switch (method) {
      case GET:
      case POST:
      case PUT:
      case DELETE:
        HttpEntity entity;
        if (request instanceof HttpEntity) {
          entity = (HttpEntity) request;
          if (!CollectionUtils.isEmpty(extraHeaders)) {
            HttpHeaders headers = new HttpHeaders();
            headers.addAll(entity.getHeaders());
            headers.addAll(extraHeaders);
            entity = new HttpEntity<>(entity.getBody(), headers);
          }
        } else {
          entity = new HttpEntity<>(request, extraHeaders);
        }
        result = restTemplate
            .exchange(parseHost(service) + path, method, entity, responseType, uriVariables)
            .getBody();
        break;
      default:
        throw new UnsupportedOperationException(String.format("unsupported http method(method=%s)", method));
    }
    return result;
  }
}
