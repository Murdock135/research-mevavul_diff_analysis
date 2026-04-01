class stringToDocument {
public static Document stringToDocument(String xml) throws Exception {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			// Disable XXE: security measure to prevent DOS, arbitrary-file-read, and possibly RCE
			dbf.setExpandEntityReferences(false);
			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			dbf.setFeature("http://xml.org/sax/features/external-general-entities",false);
			dbf.setFeature("http://xml.org/sax/features/external-parameter-entities",false);

			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(new InputSource(new StringReader(xml)));
			return document;
		}
		catch (Exception e) {
			log.error("Error converting String to Document:\n" + xml);
			throw e;
		}
	}
}
