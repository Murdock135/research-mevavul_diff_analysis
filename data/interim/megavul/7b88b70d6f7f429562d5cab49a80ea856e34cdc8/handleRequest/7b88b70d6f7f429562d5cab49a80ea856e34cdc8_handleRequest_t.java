class handleRequest {
@Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (rejectRMI()) {
            // yes, ideally, this should be short-circuited in the agent auth filter, but keeping this logic here has
            // some advantages:
            //   - it keeps all deprecated RMI logic in one place so it's easier to remove (just remove this class)
            //   - it's 100% reliable by virtue of its proximity to the RMI invocation code and can't be thwarted by
            //     some clever URI encoding to circumvent the uri path test that we would need to write at the filter
            //     level in order to selectively apply this logic to the RMI endpoint and not the JSON API endpoint
            reject(response, SC_GONE, "This RMI endpoint is disabled.");
            return;
        }

        try {
            RemoteInvocation invocation = readRemoteInvocation(request);

            if (!authorized(request, response, invocation)) {
                return;
            }

            RemoteInvocationResult result = invokeAndCreateResult(invocation, getProxy());
            writeRemoteInvocationResult(request, response, result);
        } catch (ClassNotFoundException ex) {
            throw new NestedServletException("Class not found during deserialization", ex);
        }
    }
}
