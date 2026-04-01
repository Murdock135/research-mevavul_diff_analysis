class createJSONReader {
private XMLReader createJSONReader() throws SAXException {
        return new JSONParserFactory().getParser();
    }
}
