class loadXmlFrom {
private Document loadXmlFrom(InputStream is) throws SAXException,
			IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		try {
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		} catch (ParserConfigurationException ex) {
			// All implementations are required to support FEATURE_SECURE_PROCESSING.
		}
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException ex) {
		}
		return builder.parse(is);
	}
}
