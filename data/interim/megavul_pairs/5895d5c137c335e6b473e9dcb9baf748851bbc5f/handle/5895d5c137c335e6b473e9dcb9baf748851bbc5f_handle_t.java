class handle {
@SuppressWarnings({ "PMD.AvoidCatchingThrowable", "PMD.AvoidInstanceofChecksInCatchClause" })
    private void handle(ServletRequestHandler pReqHandler,HttpServletRequest pReq, HttpServletResponse pResp) throws IOException {
        JSONAware json = null;

        try {
            // Check access policy
            requestHandler.checkAccess(allowDnsReverseLookup ? pReq.getRemoteHost() : null,
                                       pReq.getRemoteAddr(),
                                       getOriginOrReferer(pReq));

            // If a callback is given, check this is a valid javascript function name
            validateCallbackIfGiven(pReq);

            // Remember the agent URL upon the first request. Needed for discovery
            updateAgentDetailsIfNeeded(pReq);

            // Dispatch for the proper HTTP request method
            json = handleSecurely(pReqHandler, pReq, pResp);
        } catch (Throwable exp) {
            try {
                json = requestHandler.handleThrowable(
                    exp instanceof RuntimeMBeanException ? ((RuntimeMBeanException) exp).getTargetException() : exp);
            } catch (Throwable exp2) {
                exp2.printStackTrace();
            }
        } finally {
            setCorsHeader(pReq, pResp);

            if (json == null) {
                json = requestHandler.handleThrowable(new Exception("Internal error while handling an exception"));
            }

            sendResponse(pResp, pReq, json);
        }
    }
}
