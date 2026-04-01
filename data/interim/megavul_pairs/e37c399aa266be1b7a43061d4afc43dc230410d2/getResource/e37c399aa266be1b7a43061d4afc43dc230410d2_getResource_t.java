class getResource {
public Path getResource(final String resourceURI) throws IOException {
		Path resourceCachePath = getResourceCachePath(resourceURI);
		if (Files.exists(resourceCachePath)) {
			return resourceCachePath;
		}
		if (!FilesUtils.isIncludedInDeployedPath(resourceCachePath)) {
			throw new CacheResourceDownloadingException(resourceURI);
		}
		if (unavailableURICache.getIfPresent(resourceURI) != null) {
			LOGGER.info("Ignored unavailable schema URI: " + resourceURI + "\n");
			return null;
		}

		CompletableFuture<Path> f = null;
		synchronized (resourcesLoading) {
			if (resourcesLoading.containsKey(resourceURI)) {
				CompletableFuture<Path> future = resourcesLoading.get(resourceURI);
				throw new CacheResourceDownloadingException(resourceURI, future);
			}
			f = downloadResource(resourceURI, resourceCachePath);
			resourcesLoading.put(resourceURI, f);
		}

		if (f.getNow(null) == null) {
			throw new CacheResourceDownloadingException(resourceURI, f);
		}

		return resourceCachePath;
	}
}
