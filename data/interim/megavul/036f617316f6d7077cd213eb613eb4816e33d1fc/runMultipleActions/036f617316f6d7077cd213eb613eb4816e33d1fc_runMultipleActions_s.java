class runMultipleActions {
@Override
    public List<ActionReturnValue> runMultipleActions(ActionType actionType,
            ArrayList<ActionParametersBase> multipleParams, boolean isRunOnlyIfAllValidationPass, boolean isWaitForResult) {
        log.debug("Server: RunMultipleAction invoked! [amount of actions: {}]", multipleParams.size()); //$NON-NLS-1$

        String correlationId = CorrelationIdTracker.getCorrelationId();
        for (ActionParametersBase params : multipleParams) {
            params.setSessionId(getEngineSessionId());
            if (params.getCorrelationId() == null) {
                params.setCorrelationId(correlationId);
            }
        }

        List<ActionReturnValue> returnValues =
                getBackend().runMultipleActions(actionType, multipleParams, isRunOnlyIfAllValidationPass, isWaitForResult);

        return returnValues;
    }
}
