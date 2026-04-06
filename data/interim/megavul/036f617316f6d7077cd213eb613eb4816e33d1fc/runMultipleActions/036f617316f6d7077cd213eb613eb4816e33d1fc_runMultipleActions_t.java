class runMultipleActions {
@Override
    public List<ActionReturnValue> runMultipleActions(ActionType actionType,
            ArrayList<ActionParametersBase> multipleParams, boolean isRunOnlyIfAllValidationPass, boolean isWaitForResult) {
        log.debug("Server: RunMultipleAction invoked! [amount of actions: {}]", multipleParams.size()); //$NON-NLS-1$

        // CreateUserSession should never be invoked from GWT code
        if (actionType == ActionType.CreateUserSession) {
            ActionReturnValue error = new ActionReturnValue();
            error.setSucceeded(false);
            error.setFault(new EngineFault(new RuntimeException("Command cannot be executed from client"))); //$NON-NLS-1$
            return Arrays.asList(error);
        }

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
