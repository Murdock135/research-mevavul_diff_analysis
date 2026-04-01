class handleResponse {
protected void handleResponse(HttpRequest<?> request, MutableHttpResponse<?> response) {
        HttpHeaders headers = request.getHeaders();
        Optional<String> originHeader = headers.getOrigin();
        originHeader.ifPresent(requestOrigin -> {

            Optional<CorsOriginConfiguration> optionalConfig = getConfiguration(requestOrigin);

            if (optionalConfig.isPresent()) {
                CorsOriginConfiguration config = optionalConfig.get();

                if (CorsUtil.isPreflightRequest(request)) {
                    Optional<HttpMethod> result = headers.getFirst(ACCESS_CONTROL_REQUEST_METHOD, CONVERSION_CONTEXT_HTTP_METHOD);
                    setAllowMethods(result.get(), response);
                    Optional<List<String>> allowedHeaders = headers.get(ACCESS_CONTROL_REQUEST_HEADERS, ConversionContext.LIST_OF_STRING);
                    allowedHeaders.ifPresent(val ->
                        setAllowHeaders(val, response)
                    );

                    setMaxAge(config.getMaxAge(), response);
                }

                setOrigin(requestOrigin, response);
                setVary(response);
                setExposeHeaders(config.getExposedHeaders(), response);
                setAllowCredentials(config, response);
            }
        });
    }
}
