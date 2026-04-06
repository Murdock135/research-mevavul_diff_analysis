class handleCommand {
@Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            // Placeholder for later refinement
        } else {
            if (channelUID.getId().equals(RUN)) {
                if (command instanceof OnOffType) {
                    if (command == OnOffType.ON) {
                        scheduler.schedule(periodicExecutionRunnable, 0, TimeUnit.SECONDS);
                    }
                }
            } else if (channelUID.getId().equals(INPUT)) {
                if (command instanceof StringType) {
                    String previousInput = lastInput;
                    lastInput = command.toString();
                    if (lastInput != null && !lastInput.equals(previousInput)) {
                        if (getConfig().get(AUTORUN) != null && ((Boolean) getConfig().get(AUTORUN)).booleanValue()) {
                            logger.trace("Executing command '{}' after a change of the input channel to '{}'",
                                    getConfig().get(COMMAND), lastInput);
                            scheduler.schedule(periodicExecutionRunnable, 0, TimeUnit.SECONDS);
                        }
                    }
                }
            }
        }
    }
}
