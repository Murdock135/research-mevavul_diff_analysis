class loadFile {
private void loadFile(String fileStr) {
		
		String FEATURE = "";
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			
			FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
			factory.setFeature(FEATURE, true);

			FEATURE = "http://xml.org/sax/features/external-general-entities";
			factory.setFeature(FEATURE, false);

			FEATURE = "http://xml.org/sax/features/external-parameter-entities";
			factory.setFeature(FEATURE, false);

			FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
			factory.setFeature(FEATURE, false);
			
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			factory.setXIncludeAware(false);
			factory.setExpandEntityReferences(false);
			
			DocumentBuilder builder = factory.newDocumentBuilder();
			File f = new File(fileStr);
			if (f.isFile()) {
				doc = builder.parse(fileStr);
			} else {
				InputStream is = new ByteArrayInputStream(fileStr.getBytes());
				doc = builder.parse(is);
			}
			doc.getDocumentElement().normalize();
		} catch (ParserConfigurationException e) {
			logger.info(FEATURE +  " is not supported");
		} catch (SAXException e) {
			logger.warn("A DOCTYPE was passed into the XML document");
		} catch (IOException e) {
			logger.error("IOException occurred, XXE may still possible: " + e.getMessage());
		}
	}
}
