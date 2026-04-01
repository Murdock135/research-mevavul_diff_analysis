class httpCall {
private Mono<ClientResponse> httpCall(WebClient webClient, HttpMethod httpMethod, URI uri, Object requestBody,
                                              int iteration, String contentType) {
            if (iteration == MAX_REDIRECTS) {
                return Mono.error(new AppsmithPluginException(
                        AppsmithPluginError.PLUGIN_ERROR,
                        "Exceeded the HTTP redirect limits of " + MAX_REDIRECTS
                ));
            }

            assert requestBody instanceof BodyInserter<?, ?>;
            BodyInserter<?, ?> finalRequestBody = (BodyInserter<?, ?>) requestBody;

            return webClient
                    .method(httpMethod)
                    .uri(uri)
                    .body((BodyInserter<?, ? super ClientHttpRequest>) finalRequestBody)
                    .exchange()
                    .doOnError(e -> Mono.error(new AppsmithPluginException(AppsmithPluginError.PLUGIN_ERROR, e)))
                    .flatMap(response -> {
                        if (response.statusCode().is3xxRedirection()) {
                            String redirectUrl = response.headers().header("Location").get(0);
                            /**
                             * TODO
                             * In case the redirected URL is not absolute (complete), create the new URL using the relative path
                             * This particular scenario is seen in the URL : https://rickandmortyapi.com/api/character
                             * It redirects to partial URI : /api/character/
                             * In this scenario we should convert the partial URI to complete URI
                             */
                            URI redirectUri = null;
                            try {
                                redirectUri = new URI(redirectUrl);
                            } catch (URISyntaxException e) {
                                return Mono.error(new AppsmithPluginException(AppsmithPluginError.PLUGIN_ERROR, e));
                            }
                            return httpCall(webClient, httpMethod, redirectUri, finalRequestBody, iteration + 1,
                                    contentType);
                        }
                        return Mono.just(response);
                    });
        }
}
