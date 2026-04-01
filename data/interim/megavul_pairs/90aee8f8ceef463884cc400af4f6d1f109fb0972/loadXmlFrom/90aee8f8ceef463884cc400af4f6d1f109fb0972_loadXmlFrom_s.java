class loadXmlFrom {
private Document loadXmlFrom(InputStream is) throws SAXException,
			IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException ex) {
		}
		return builder.parse(is);
	}
}
