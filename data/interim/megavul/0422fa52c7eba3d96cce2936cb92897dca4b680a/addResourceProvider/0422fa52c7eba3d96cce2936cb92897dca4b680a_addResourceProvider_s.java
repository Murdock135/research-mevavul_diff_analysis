class addResourceProvider {
public void addResourceProvider(String key, ResourceProvider provider) {
	    if (resourceProviders == null) {
			resourceProviders = new LinkedHashMap<String, ResourceProvider>();
		}

		boolean addedInDevMode = UiFrameworkUtil.checkAndSetDevelopmentModeForProvider(key, provider);
		if (addedInDevMode) {
			resourceProvidersInDevelopmentMode.add(key);
		}
		
		resourceProviders.put(key, provider);
    }
}
