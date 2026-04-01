class extractText_1 {
public String extractText(InputStream stream, String type, String encoding) throws IOException {
		try {
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			saxParserFactory.setValidating(false);
			SAXParser saxParser = saxParserFactory.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			xmlReader.setFeature("http://xml.org/sax/features/validation", false);
			xmlReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

			ZipInputStream zis = new ZipInputStream(stream);
			ZipEntry ze = zis.getNextEntry();

			while (ze != null && !ze.getName().equals("content.xml")) {
				ze = zis.getNextEntry();
			}

			OpenOfficeContentHandler contentHandler = new OpenOfficeContentHandler();
			xmlReader.setContentHandler(contentHandler);

			try {
				xmlReader.parse(new InputSource(zis));
			} finally {
				zis.close();
			}

			return contentHandler.getContent();
		} catch (ParserConfigurationException | SAXException e) {
			logger.warn("Failed to extract OpenOffice text content", e);
			throw new IOException(e.getMessage(), e);
		} finally {
			stream.close();
		}
	}
}
