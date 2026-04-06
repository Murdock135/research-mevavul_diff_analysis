class resolveIpAddresses {
private List<ADnsAnswer> resolveIpAddresses(String hostName, DnsRecordType dnsRecordType, boolean includeIpVersion)
            throws InterruptedException, ExecutionException {

        LOG.debug("Attempting to resolve [{}] records for [{}]", dnsRecordType, hostName);

        if (isShutdown()) {
            throw new DnsClientNotRunningException();
        }

        validateHostName(hostName);

        final DefaultDnsQuestion aRecordDnsQuestion = new DefaultDnsQuestion(hostName, dnsRecordType);

        /* The DnsNameResolver.resolveAll(DnsQuestion) method handles all redirects through CNAME records to
         * ultimately resolve a list of IP addresses with TTL values. */
        try {
            return resolver.resolveAll(aRecordDnsQuestion).get(requestTimeout, TimeUnit.MILLISECONDS).stream()
                           .map(dnsRecord -> decodeDnsRecord(dnsRecord, includeIpVersion))
                           .filter(Objects::nonNull) // Removes any entries which the IP address could not be extracted for.
                           .collect(Collectors.toList());
        } catch (TimeoutException e) {
            throw new ExecutionException("Resolver future didn't return a result in " + requestTimeout + " ms", e);
        }
    }
}
