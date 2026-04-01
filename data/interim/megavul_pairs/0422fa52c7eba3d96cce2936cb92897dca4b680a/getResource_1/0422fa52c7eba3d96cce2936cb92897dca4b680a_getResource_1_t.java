class getResource_1 {
public File getResource(String providerName, String resourcePath) {
		if (resourcePath == null) {
			return null;
		}

		if (!resourcePath.equals(FilenameUtils.normalize(resourcePath))) {
			log.warn("Attempted to load file via directory traversal using path: {}", resourcePath);
			return null;
		}

		if (providerName == null) {
			for (ResourceProvider provider : resourceProviders.values()) {
				File ret = provider.getResource(resourcePath);
				if (ret != null)
					return ret;
			}
			// not found in any registered provider
			return null;
		} else {
			ResourceProvider provider = resourceProviders.get(providerName);
			return provider.getResource(resourcePath);
		}
	}
}
