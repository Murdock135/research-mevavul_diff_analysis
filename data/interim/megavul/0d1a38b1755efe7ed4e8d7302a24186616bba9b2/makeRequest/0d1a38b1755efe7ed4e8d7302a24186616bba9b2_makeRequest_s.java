class makeRequest {
protected HttpUriRequest makeRequest() throws IOException, MessageInvalidException {
        final String requestUri = getRequestUri();
        HttpUriRequest req;
        if (mAttemptNumber == ATTEMPT_UNAUTHENTICATED_GET) {
            req = mConnection.makeGet(requestUri);
        } else {
            req = mConnection.makePost(requestUri, getRequestEntity(),
                    getRequestContentType(), addPolicyKeyHeaderToRequest());
        }
        return req;
    }
}
