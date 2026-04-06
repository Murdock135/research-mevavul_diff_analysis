class getHttpResponse {
private CloseableHttpResponse getHttpResponse(BaseMessage message) throws Exception {
        URI uri = null;
        HttpRequestBase httpRequest;
        if (message instanceof CustomWebhookMessage) {
            CustomWebhookMessage customWebhookMessage = (CustomWebhookMessage) message;
            uri = customWebhookMessage.getUri();
            httpRequest = constructHttpRequest(((CustomWebhookMessage) message).getMethod());
            // set headers
            Map<String, String> headerParams = customWebhookMessage.getHeaderParams();
            if(headerParams == null || headerParams.isEmpty()) {
                // set default header
                httpRequest.setHeader("Content-Type", "application/json");
            } else {
                for (Map.Entry<String, String> e : customWebhookMessage.getHeaderParams().entrySet())
                    httpRequest.setHeader(e.getKey(), e.getValue());
            }
        } else {
             httpRequest = new HttpPost();
             uri = message.getUri();
        }

        httpRequest.setURI(uri);
        if (httpRequest instanceof HttpEntityEnclosingRequestBase){
            StringEntity entity = new StringEntity(extractBody(message), StandardCharsets.UTF_8);
            ((HttpEntityEnclosingRequestBase) httpRequest).setEntity(entity);
        }

        return HTTP_CLIENT.execute(httpRequest);
    }
}
