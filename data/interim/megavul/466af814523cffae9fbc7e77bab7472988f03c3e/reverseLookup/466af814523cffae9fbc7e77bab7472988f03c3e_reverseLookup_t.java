class reverseLookup {
public PtrDnsAnswer reverseLookup(String ipAddress) throws InterruptedException, ExecutionException {

        LOG.debug("Attempting to perform reverse lookup for IP address [{}]", ipAddress);

        if (resolverPool.isStopped()) {
            throw new DnsClientNotRunningException();
        }

        validateIpAddress(ipAddress);

        final String inverseAddressFormat = getInverseAddressFormat(ipAddress);

        DnsResponse content = null;
        final ResolverLease resolverLease = resolverPool.takeLease();
        try {
            content = resolverLease.getResolver().query(new DefaultDnsQuestion(inverseAddressFormat, DnsRecordType.PTR)).get(requestTimeout, TimeUnit.MILLISECONDS).content();
            for (int i = 0; i < content.count(DnsSection.ANSWER); i++) {

                // Return the first PTR record, because there should be only one as per
                // http://tools.ietf.org/html/rfc1035#section-3.5
                final DnsRecord dnsRecord = content.recordAt(DnsSection.ANSWER, i);
                if (dnsRecord instanceof DefaultDnsPtrRecord) {

                    final DefaultDnsPtrRecord ptrRecord = (DefaultDnsPtrRecord) dnsRecord;
                    final PtrDnsAnswer.Builder dnsAnswerBuilder = PtrDnsAnswer.builder();

                    final String hostname = ptrRecord.hostname();
                    LOG.trace("PTR record retrieved with hostname [{}]", hostname);

                    try {
                        parseReverseLookupDomain(dnsAnswerBuilder, hostname);
                    } catch (IllegalArgumentException e) {
                        LOG.debug("Reverse lookup of [{}] was partially successful. The DNS server returned [{}], " +
                                  "which is an invalid host name. The \"domain\" field will be left blank.",
                                  ipAddress, hostname);
                        dnsAnswerBuilder.domain("");
                    }

                    return dnsAnswerBuilder.dnsTTL(ptrRecord.timeToLive())
                                           .build();
                }
            }
        } catch (TimeoutException e) {
            throw new ExecutionException("Resolver future didn't return a result in " + requestTimeout + " ms", e);
        } finally {
            if (content != null) {
                // Must manually release references on content object since the DnsResponse class extends ReferenceCounted
                content.release();
            }
            resolverPool.returnLease(resolverLease);
        }

        return null;
    }
}
