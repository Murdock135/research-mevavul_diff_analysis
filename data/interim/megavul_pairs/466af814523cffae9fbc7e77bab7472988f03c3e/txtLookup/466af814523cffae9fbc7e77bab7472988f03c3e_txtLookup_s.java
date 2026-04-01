class txtLookup {
public List<TxtDnsAnswer> txtLookup(String hostName) throws InterruptedException, ExecutionException {

        if (isShutdown()) {
            throw new DnsClientNotRunningException();
        }

        LOG.debug("Attempting to perform TXT lookup for hostname [{}]", hostName);

        validateHostName(hostName);

        DnsResponse content = null;
        try {
            content = resolver.query(new DefaultDnsQuestion(hostName, DnsRecordType.TXT)).get(requestTimeout, TimeUnit.MILLISECONDS).content();
            int count = content.count(DnsSection.ANSWER);
            final ArrayList<TxtDnsAnswer> txtRecords = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {

                final DnsRecord dnsRecord = content.recordAt(DnsSection.ANSWER, i);
                LOG.trace("TXT record [{}] retrieved with content [{}].", i, dnsRecord);

                if (dnsRecord instanceof DefaultDnsRawRecord) {
                    final DefaultDnsRawRecord txtRecord = (DefaultDnsRawRecord) dnsRecord;

                    final TxtDnsAnswer.Builder dnsAnswerBuilder = TxtDnsAnswer.builder();
                    final String decodeTxtRecord = decodeTxtRecord(txtRecord);
                    LOG.trace("The decoded TXT record is [{}]", decodeTxtRecord);

                    dnsAnswerBuilder.value(decodeTxtRecord)
                                    .dnsTTL(txtRecord.timeToLive())
                                    .build();

                    txtRecords.add(dnsAnswerBuilder.build());
                }
            }

            return txtRecords;
        } catch (TimeoutException e) {
            throw new ExecutionException("Resolver future didn't return a result in " + requestTimeout + " ms", e);
        } finally {
            if (content != null) {
                // Must manually release references on content object since the DnsResponse class extends ReferenceCounted
                content.release();
            }
        }
    }
}
