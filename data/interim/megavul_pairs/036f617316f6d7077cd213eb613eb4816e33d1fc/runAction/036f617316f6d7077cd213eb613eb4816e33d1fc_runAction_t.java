class runAction {
@Override
    public ActionReturnValue runAction(ActionType actionType,
            ActionParametersBase params) {
        log.debug("Server: RunAction invoked!"); //$NON-NLS-1$
        debugAction(actionType, params);

        // CreateUserSession should never be invoked from GWT code
        if (actionType == ActionType.CreateUserSession) {
            ActionReturnValue error = new ActionReturnValue();
            error.setSucceeded(false);
            error.setFault(new EngineFault(new RuntimeException("Command cannot be executed from client"))); //$NON-NLS-1$
            return error;
        }

        params.setSessionId(getEngineSessionId());
        if (params.getCorrelationId() == null) {
            params.setCorrelationId(CorrelationIdTracker.getCorrelationId());
        }

        return getBackend().runAction(actionType, params);
    }
}
