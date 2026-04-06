class getResponseXML {
public synchronized Document getResponseXML() {
		byte[] bytes = this.responseBytes;
		if (bytes == null) {
			return null;
		}
		InputStream in = new ByteArrayInputStream(bytes);
		String FEATURE = "";
		try {

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

			FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
			factory.setFeature(FEATURE, true);

			FEATURE = "http://xml.org/sax/features/external-general-entities";
			factory.setFeature(FEATURE, false);

			FEATURE = "http://xml.org/sax/features/external-parameter-entities";
			factory.setFeature(FEATURE, false);

			FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
			factory.setFeature(FEATURE, false);

			factory.setXIncludeAware(false);
			factory.setExpandEntityReferences(false);

			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(in);
		} catch (ParserConfigurationException e) {
			logger.info(FEATURE + " is not supported");
			return null;
		} catch (SAXException e) {
			logger.warn("A DOCTYPE was passed into the XML document");
			return null;
		} catch (IOException e) {
			logger.error("IOException occurred, XXE may still possible: " + e.getMessage());
			return null;
		}
	}
}
