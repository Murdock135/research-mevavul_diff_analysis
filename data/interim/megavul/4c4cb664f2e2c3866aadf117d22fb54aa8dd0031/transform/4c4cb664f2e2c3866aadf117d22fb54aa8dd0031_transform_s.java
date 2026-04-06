class transform {
@Override
    public @Nullable String transform(String commandLine, String source) throws TransformationException {
        if (commandLine == null || source == null) {
            throw new TransformationException("the given parameters 'commandLine' and 'source' must not be null");
        }

        logger.debug("about to transform '{}' by the commandline '{}'", source, commandLine);

        long startTime = System.currentTimeMillis();

        String formattedCommandLine = String.format(commandLine, source);
        String result = ExecUtil.executeCommandLineAndWaitResponse(formattedCommandLine, 5000);
        logger.trace("command line execution elapsed {} ms", System.currentTimeMillis() - startTime);

        return result;
    }
}
