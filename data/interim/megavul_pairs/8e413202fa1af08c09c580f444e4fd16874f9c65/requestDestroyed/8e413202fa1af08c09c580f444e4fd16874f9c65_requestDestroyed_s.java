class requestDestroyed {
public void requestDestroyed(HttpServletRequest request) {
        if (isRequestDestroyed(request)) {
            return;
        }
        if (nestedInvocationGuardEnabled) {
            Counter counter = nestedInvocationGuard.get();
            if (counter != null) {
                counter.value--;
                if (counter.value > 0) {
                    return; // this is a nested invocation, ignore it
                } else {
                    nestedInvocationGuard.remove(); // this is the outer invocation
                    request.removeAttribute(GUARD_PARAMETER_NAME);
                }
            } else {
                ServletLogger.LOG.guardNotSet();
                return;
            }
        }
        if (ignoreForwards && isForwardedRequest(request)) {
            return;
        }
        if (ignoreIncludes && isIncludedRequest(request)) {
            return;
        }
        if (!contextActivationFilter.accepts(request)) {
            return;
        }

        ServletLogger.LOG.requestDestroyed(request);

        try {
            conversationContextActivator.deactivateConversationContext(request);
            /*
             * if this request has been switched to async then do not invalidate the context now
             * as it will be invalidated at the end of the async operation.
             */
            if (!servletApi.isAsyncSupported() || !servletApi.isAsyncStarted(request)) {
                getRequestContext().invalidate();
            }
            getRequestContext().deactivate();
            // fire @Destroyed(RequestScoped.class)
            requestDestroyedEvent.fire(request);
            getSessionContext().deactivate();
            // fire @Destroyed(SessionScoped.class)
            if (!getSessionContext().isValid()) {
                sessionDestroyedEvent.fire((HttpSession) request.getAttribute(HTTP_SESSION));
            }
        } finally {
            getRequestContext().dissociate(request);

            // WFLY-1533 Underlying HTTP session may be invalid
            try {
                getSessionContext().dissociate(request);
            } catch (Exception e) {
                ServletLogger.LOG.unableToDissociateContext(getSessionContext(), request);
                ServletLogger.LOG.catchingDebug(e);
            }
            // Catch block is inside the activator method so that we're able to log the context
            conversationContextActivator.disassociateConversationContext(request);

            SessionHolder.clear();
        }
    }
}
