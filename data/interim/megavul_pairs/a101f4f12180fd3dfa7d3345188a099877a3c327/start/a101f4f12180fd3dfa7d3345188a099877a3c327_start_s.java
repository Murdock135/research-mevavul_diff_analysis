class start {
public void start(String dnsServerIps) {

        LOG.debug("Attempting to start DNS client");
        final List<InetSocketAddress> iNetDnsServerIps = parseServerIpAddresses(dnsServerIps);

        nettyEventLoop = new NioEventLoopGroup();

        final DnsNameResolverBuilder dnsNameResolverBuilder = new DnsNameResolverBuilder(nettyEventLoop.next());
        dnsNameResolverBuilder.channelType(NioDatagramChannel.class).queryTimeoutMillis(queryTimeout);

        // Specify custom DNS servers if provided. If not, use those specified in local network adapter settings.
        if (CollectionUtils.isNotEmpty(iNetDnsServerIps)) {

            LOG.debug("Attempting to start DNS client with server IPs [{}] on port [{}] with timeout [{}]",
                      dnsServerIps, DEFAULT_DNS_PORT, requestTimeout);

            final DnsServerAddressStreamProvider dnsServer = new SequentialDnsServerAddressStreamProvider(iNetDnsServerIps);
            dnsNameResolverBuilder.nameServerProvider(dnsServer);
        } else {
            LOG.debug("Attempting to start DNS client with the local network adapter DNS server address on port [{}] with timeout [{}]",
                      DEFAULT_DNS_PORT, requestTimeout);
        }

        resolver = dnsNameResolverBuilder.build();

        LOG.debug("DNS client startup successful");
    }
}
