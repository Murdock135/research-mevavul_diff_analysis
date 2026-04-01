class runAction {
@Override
    public ActionReturnValue runAction(ActionType actionType,
            ActionParametersBase params) {
        log.debug("Server: RunAction invoked!"); //$NON-NLS-1$
        debugAction(actionType, params);
        params.setSessionId(getEngineSessionId());
        if (params.getCorrelationId() == null) {
            params.setCorrelationId(CorrelationIdTracker.getCorrelationId());
        }

        return getBackend().runAction(actionType, params);
    }
}
