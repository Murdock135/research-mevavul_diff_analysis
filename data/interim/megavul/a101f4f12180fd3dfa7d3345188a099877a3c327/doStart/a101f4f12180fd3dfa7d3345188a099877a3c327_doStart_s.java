class doStart {
@Override
    protected void doStart() {

        dnsClient = new DnsClient(config.requestTimeout());
        dnsClient.start(config.serverIps());
    }
}
