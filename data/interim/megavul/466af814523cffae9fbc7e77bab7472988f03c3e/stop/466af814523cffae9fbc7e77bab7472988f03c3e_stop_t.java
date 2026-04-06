class stop {
public void stop() {
        LOG.debug("Attempting to stop DNS client");
        if (resolverPool == null) {
            LOG.error("DNS resolution pool is not initialized.");
            return;
        }
        resolverPool.stop();
    }
}
