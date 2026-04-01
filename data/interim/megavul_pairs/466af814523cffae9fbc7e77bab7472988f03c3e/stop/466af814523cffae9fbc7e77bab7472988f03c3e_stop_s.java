class stop {
public void stop() {

        LOG.debug("Attempting to stop DNS client");

        if (nettyEventLoop == null) {
            LOG.error("DNS resolution event loop not initialized");
            return;
        }

        // Make sure to close the resolver before shutting down the event loop
        resolver.close();

        // Shutdown event loop (required by Netty).
        final Future<?> shutdownFuture = nettyEventLoop.shutdownGracefully();
        shutdownFuture.addListener(future -> LOG.debug("DNS client shutdown successful"));
    }
}
