class initialize {
@Override
    public void initialize() throws InitializationException
    {
        if (!this.configuration.isEnabled()) {
            return;
        }

        CacheConfiguration cacheConfig = new CacheConfiguration();
        cacheConfig.setConfigurationId("diff.html.dataURI");
        LRUEvictionConfiguration lru = new LRUEvictionConfiguration();
        lru.setMaxEntries(100);
        cacheConfig.put(EntryEvictionConfiguration.CONFIGURATIONID, lru);

        CacheConfiguration failureCacheConfiguration = new CacheConfiguration();
        failureCacheConfiguration.setConfigurationId("diff.html.dataURIFailureCache");
        LRUEvictionConfiguration failureLRU = new LRUEvictionConfiguration();
        failureLRU.setMaxEntries(1000);
        // Cache failures for an hour. This is to avoid hammering the server with requests for images that don't
        // exist or are inaccessible or too large.
        failureLRU.setLifespan(3600);
        failureCacheConfiguration.put(EntryEvictionConfiguration.CONFIGURATIONID, failureLRU);

        try {
            this.cache = this.cacheManager.createNewCache(cacheConfig);
            this.failureCache = this.cacheManager.createNewCache(failureCacheConfiguration);
        } catch (Exception e) {
            // Dispose the cache if it has been created.
            if (this.cache != null) {
                this.cache.dispose();
            }
            throw new InitializationException("Failed to create the Data URI cache.", e);
        }
    }
}
