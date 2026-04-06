class getResource_1 {
public File getResource(String providerName, String resourcePath) {
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
