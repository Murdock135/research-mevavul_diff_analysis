class resolveIpAddresses {
private List<ADnsAnswer> resolveIpAddresses(String hostName, DnsRecordType dnsRecordType, boolean includeIpVersion)
            throws InterruptedException, ExecutionException {

        LOG.debug("Attempting to resolve [{}] records for [{}]", dnsRecordType, hostName);

        if (resolverPool.isStopped()) {
            throw new DnsClientNotRunningException();
        }

        validateHostName(hostName);

        final DefaultDnsQuestion aRecordDnsQuestion = new DefaultDnsQuestion(hostName, dnsRecordType);

        final ResolverLease resolverLease = resolverPool.takeLease();
        /* The DnsNameResolver.resolveAll(DnsQuestion) method handles all redirects through CNAME records to
         * ultimately resolve a list of IP addresses with TTL values. */
        try {
            return resolverLease.getResolver().resolveAll(aRecordDnsQuestion).get(requestTimeout, TimeUnit.MILLISECONDS).stream()
                           .map(dnsRecord -> decodeDnsRecord(dnsRecord, includeIpVersion))
                           .filter(Objects::nonNull) // Removes any entries which the IP address could not be extracted for.
                           .collect(Collectors.toList());
        } catch (TimeoutException e) {
            throw new ExecutionException("Resolver future didn't return a result in " + requestTimeout + " ms", e);
        }
        finally {
            resolverPool.returnLease(resolverLease);
        }
    }
}
