class setAdditionalResourceProviders {
public void setAdditionalResourceProviders(Map<String, ResourceProvider> additional) {
		for (Map.Entry<String, ResourceProvider> e : additional.entrySet()) {
			addResourceProvider(e.getKey(), e.getValue());
		}
	}
}
