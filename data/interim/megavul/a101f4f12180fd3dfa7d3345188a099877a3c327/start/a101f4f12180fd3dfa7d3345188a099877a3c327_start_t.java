class start {
public void start(String dnsServerIps) {
        LOG.debug("Attempting to start DNS client");
        this.resolverPool = new DnsResolverPool(dnsServerIps, queryTimeout, resolverPoolSize, resolverPoolRefreshSeconds);
        this.resolverPool.initialize();
    }
}
