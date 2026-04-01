class initialize {
@Override
    public void initialize() throws InitializationException
    {
        CacheConfiguration cacheConfig = new CacheConfiguration();
        cacheConfig.setConfigurationId("diff.html.dataURI");
        LRUEvictionConfiguration lru = new LRUEvictionConfiguration();
        lru.setMaxEntries(100);
        cacheConfig.put(LRUEvictionConfiguration.CONFIGURATIONID, lru);
        try {
            this.cache = this.cacheManager.createNewCache(cacheConfig);
        } catch (Exception e) {
            throw new InitializationException("Failed to create the Data URI cache.", e);
        }
    }
}
