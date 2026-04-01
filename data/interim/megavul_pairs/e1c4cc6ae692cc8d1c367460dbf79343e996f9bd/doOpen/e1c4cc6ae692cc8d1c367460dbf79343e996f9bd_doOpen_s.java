class doOpen {
public void doOpen() {
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			FileInputStream input = new FileInputStream(file);
			InputHandler xmlhandler = new InputHandler(handler);
			parser.parse(input, xmlhandler);
			input.close();
		} catch (Exception e) {
			log.error("Cannot open the file: " + file.getAbsolutePath(), e);

		}
	}
}
