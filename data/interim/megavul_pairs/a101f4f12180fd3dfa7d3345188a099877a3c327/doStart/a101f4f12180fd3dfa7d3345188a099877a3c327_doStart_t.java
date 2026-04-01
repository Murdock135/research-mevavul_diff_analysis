class doStart {
@Override
    protected void doStart() {
        dnsClient = new DnsClient(config.requestTimeout(), adapterConfiguration.getPoolSize(),
                adapterConfiguration.getPoolRefreshInterval().toSeconds());
        dnsClient.start(config.serverIps());
    }
}
