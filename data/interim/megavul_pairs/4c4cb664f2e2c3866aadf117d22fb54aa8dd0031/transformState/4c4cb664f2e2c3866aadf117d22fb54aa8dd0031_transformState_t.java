class transformState {
private Type transformState(Type state) {
        String result = state.toFullString();
        String function = this.function;
        String sourceFormat = this.sourceFormat;

        if (function == null || sourceFormat == null) {
            logger.warn("Could not transform state '{}' with function '{}' and format '{}'", state, function,
                    sourceFormat);
        } else {
            try {
                result = TransformationHelper.transform(service, function, sourceFormat, state.toFullString());
            } catch (TransformationException e) {
                logger.warn("Could not transform state '{}' with function '{}' and format '{}'", state, function,
                        sourceFormat);
            }
        }
        StringType resultType = new StringType(result);
        logger.debug("Transformed '{}' into '{}'", state, resultType);
        return resultType;
    }
}
