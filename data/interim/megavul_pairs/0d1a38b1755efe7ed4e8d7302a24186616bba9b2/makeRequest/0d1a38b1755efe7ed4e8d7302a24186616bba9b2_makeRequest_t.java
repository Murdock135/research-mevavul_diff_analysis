class makeRequest {
protected HttpUriRequest makeRequest() throws IOException, MessageInvalidException {
        final String requestUri = getRequestUri();
        final HttpUriRequest req = mConnection.makePost(requestUri, getRequestEntity(),
                getRequestContentType(), addPolicyKeyHeaderToRequest());
        // Disable auto-redirecting for this request.
        HttpClientParams.setRedirecting(req.getParams(), false);
        return req;
    }
}
