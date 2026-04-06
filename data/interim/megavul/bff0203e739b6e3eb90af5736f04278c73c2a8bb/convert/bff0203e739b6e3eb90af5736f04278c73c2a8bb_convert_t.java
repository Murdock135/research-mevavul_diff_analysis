class convert {
@Override
    public String convert(String url) throws DiffException
    {
        if (url.startsWith("data:") || !this.configuration.isEnabled()) {
            // Already data URI.
            return url;
        }

        // Convert URL to absolute URL to avoid issues with relative URLs that might reference different images
        // in different subwikis.
        URL absoluteURL = getAbsoluteURL(url, this.xcontextProvider.get());

        String cacheKey = getCacheKey(absoluteURL);

        try {
            String dataURI = this.cache.get(cacheKey);

            if (dataURI == null) {
                DiffException failure = this.failureCache.get(cacheKey);

                if (failure != null) {
                    throw failure;
                }

                dataURI = convert(absoluteURL);
                this.cache.set(cacheKey, dataURI);
            }

            return dataURI;
        } catch (IOException | URISyntaxException e) {
            DiffException diffException = new DiffException("Failed to convert [" + url + "] to data URI.", e);
            this.failureCache.set(cacheKey, diffException);
            throw diffException;
        }
    }
}
