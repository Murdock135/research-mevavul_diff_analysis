class authorized {
private boolean authorized(HttpServletRequest request, HttpServletResponse response, RemoteInvocation invocation) throws IOException {
        final String uuid = request.getHeader("X-Agent-GUID"); // should never be null since we passed the auth filter
        final MethodSignature current = new MethodSignature(invocation);

        LOG.debug(format("Checking authorization for agent [%s] on invocation: %s", uuid, invocation));

        if (KNOWN_METHODS_NEEDING_UUID_VALIDATION.contains(current)) {
            final String askingFor = AgentUUID.fromRuntimeInfo0(invocation.getArguments());

            if (!uuid.equals(askingFor)) {
                LOG.error(format("DENYING REQUEST: Agent [%s] is attempting a request on behalf of [%s]: %s", uuid, askingFor, invocation));
                reject(response, SC_FORBIDDEN, "Not allowing request on behalf of another agent");
                return false;
            }
        } else {
            LOG.error(format("DENYING REQUEST: Agent [%s] is requesting an unknown method invocation: %s", uuid, invocation));
            reject(response, SC_BAD_REQUEST, format("Unknown invocation: %s", invocation));
            return false;
        }

        LOG.debug(format("ALLOWING REQUEST: Agent [%s] is authorized to invoke: %s", uuid, invocation));
        return true;
    }
}
