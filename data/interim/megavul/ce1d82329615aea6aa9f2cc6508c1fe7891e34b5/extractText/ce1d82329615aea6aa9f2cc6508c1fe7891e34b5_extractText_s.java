class extractText {
public String extractText(InputStream stream, String type, String encoding) throws IOException {
		try {
			CharArrayWriter writer = new CharArrayWriter();
			ExtractorHandler handler = new ExtractorHandler(writer);

			// TODO: Use a pull parser to avoid the memory overhead
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			reader.setContentHandler(handler);
			reader.setErrorHandler(handler);

			// It is unspecified whether the XML parser closes the stream when
			// done parsing. To ensure that the stream gets closed just once,
			// we prevent the parser from closing it by catching the close()
			// call and explicitly close the stream in a finally block.
			InputSource source = new InputSource(new FilterInputStream(stream) {
				public void close() {
				}
			});

			if (encoding != null) {
				try {
					Charset.forName(encoding);
					source.setEncoding(encoding);
				} catch (Exception e) {
					logger.warn("Unsupported encoding '{}', using default ({}) instead.",
							new Object[]{encoding, System.getProperty("file.encoding")});
				}
			}

			reader.parse(source);
			return writer.toString();
		} catch (ParserConfigurationException | SAXException e) {
			logger.warn("Failed to extract XML text content", e);
			throw new IOException(e.getMessage(), e);
		} finally {
			stream.close();
		}
	}
}
