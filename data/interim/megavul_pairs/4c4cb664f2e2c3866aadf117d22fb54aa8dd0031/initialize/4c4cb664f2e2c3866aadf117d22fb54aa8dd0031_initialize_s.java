class initialize {
@Override
    public void initialize() {
        if (executionJob == null || executionJob.isCancelled()) {
            if (((BigDecimal) getConfig().get(INTERVAL)) != null
                    && ((BigDecimal) getConfig().get(INTERVAL)).intValue() > 0) {
                int pollingInterval = ((BigDecimal) getConfig().get(INTERVAL)).intValue();
                executionJob = scheduler.scheduleWithFixedDelay(periodicExecutionRunnable, 0, pollingInterval,
                        TimeUnit.SECONDS);
            }
        }

        updateStatus(ThingStatus.ONLINE);
    }
}
