class getCommandConfigurationBeans {
@Override
    protected List<Object> getCommandConfigurationBeans() {
        return Arrays.asList(configuration,
                httpConfiguration,
                elasticsearchConfiguration,
                elasticsearchClientConfiguration,
                emailConfiguration,
                mongoDbConfiguration,
                versionCheckConfiguration,
                kafkaJournalConfiguration,
                nettyTransportConfiguration,
                pipelineConfiguration,
                viewsConfiguration,
                processingStatusConfig,
                jobSchedulerConfiguration,
                prometheusExporterConfiguration,
                tlsConfiguration,
                geoIpProcessorConfig,
                telemetryConfiguration,
                dnsLookupAdapterConfiguration);
    }
}
