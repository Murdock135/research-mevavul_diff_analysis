class loadFile {
private void loadFile(String fileStr) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			File f = new File(fileStr);
			if (f.isFile()) {
				doc = builder.parse(fileStr);
			} else {
				InputStream is = new ByteArrayInputStream(fileStr.getBytes());
				doc = builder.parse(is);
			}
			doc.getDocumentElement().normalize();
		} catch (SAXException | IOException | ParserConfigurationException e) {
			logger.error(e);
		}
	}
}
