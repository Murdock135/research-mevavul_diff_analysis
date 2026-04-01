class getResourceAsString {
public String getResourceAsString(String providerName, String resourcePath) throws IOException {
		File file = getResource(providerName, resourcePath);
		if (file == null) {
			return null;
		}
		return OpenmrsUtil.getFileAsString(file);
	}
}
