class convert {
@Override
    public String convert(String url) throws DiffException
    {
        if (url.startsWith("data:")) {
            // Already data URI.
            return url;
        }

        String cachedDataURI = this.cache.get(url);
        if (cachedDataURI == null) {
            try {
                cachedDataURI = convert(getAbsoluteURI(url));
                this.cache.set(url, cachedDataURI);
            } catch (IOException | URISyntaxException e) {
                throw new DiffException("Failed to convert [" + url + "] to data URI.", e);
            }
        }

        return cachedDataURI;
    }
}
