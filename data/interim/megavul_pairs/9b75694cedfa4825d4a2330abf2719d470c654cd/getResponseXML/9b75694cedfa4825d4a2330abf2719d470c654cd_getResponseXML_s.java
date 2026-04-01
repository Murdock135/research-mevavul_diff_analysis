class getResponseXML {
public synchronized Document getResponseXML() {
		byte[] bytes = this.responseBytes;
		if (bytes == null) {
			return null;
		}
		InputStream in = new ByteArrayInputStream(bytes);
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
		} catch (Exception err) {
			logger.error("Unable to parse response as XML.", err);
			return null;
		}
	}
}
