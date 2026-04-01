class handleRequest {
protected Optional<MutableHttpResponse<?>> handleRequest(HttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        Optional<String> originHeader = headers.getOrigin();
        if (originHeader.isPresent()) {

            String requestOrigin = originHeader.get();
            boolean preflight = CorsUtil.isPreflightRequest(request);

            Optional<CorsOriginConfiguration> optionalConfig = getConfiguration(requestOrigin);

            if (optionalConfig.isPresent()) {
                CorsOriginConfiguration config = optionalConfig.get();

                HttpMethod requestMethod = request.getMethod();

                List<HttpMethod> allowedMethods = config.getAllowedMethods();

                if (!isAnyMethod(allowedMethods)) {
                    HttpMethod methodToMatch = preflight ? headers.getFirst(ACCESS_CONTROL_REQUEST_METHOD, HttpMethod.class).orElse(requestMethod) : requestMethod;
                    if (allowedMethods.stream().noneMatch(method -> method.equals(methodToMatch))) {
                        return Optional.of(HttpResponse.status(HttpStatus.FORBIDDEN));
                    }
                }

                if (preflight) {
                    Optional<List<String>> accessControlHeaders = headers.get(ACCESS_CONTROL_REQUEST_HEADERS, Argument.listOf(String.class));

                    List<String> allowedHeaders = config.getAllowedHeaders();

                    if (!isAny(allowedHeaders) && accessControlHeaders.isPresent()) {
                        if (!accessControlHeaders.get().stream()
                            .allMatch(header -> allowedHeaders.stream()
                                .anyMatch(allowedHeader -> allowedHeader.equals(header.trim())))) {
                            return Optional.of(HttpResponse.status(HttpStatus.FORBIDDEN));
                        }
                    }

                    MutableHttpResponse<Object> ok = HttpResponse.ok();
                    handleResponse(request, ok);
                    return Optional.of(ok);
                }
            }
        }

        return Optional.empty();
    }
}
