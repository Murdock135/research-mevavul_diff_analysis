class readDocument {
public static Document readDocument(File f) throws IOException, SAXException, ParserConfigurationException {
		Document document = null;

		DocumentBuilderFactory factory = XMLUtils.safeDocumentBuilderFactory();
		// factory.setValidating(true);
		// factory.setNamespaceAware(true);

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(f);

			// displayDocument(document);

		} catch (SAXException sxe) {
			// Error generated during parsing)
			Exception x = sxe;
			if (sxe.getException() != null)
				x = sxe.getException();
			x.printStackTrace();
			throw sxe;
		} catch (ParserConfigurationException pce) {
			// Parser with specified options can't be built
			pce.printStackTrace();
			throw pce;
		} catch (IOException ioe) {
			// I/O error
			ioe.printStackTrace();
			throw ioe;
		}

		return document;
	}
}
