class createXMLReader {
private XMLReader createXMLReader() throws SAXException {
        SAXParser parser = null;
        try {
            parser = PARSER_FACTORY.newSAXParser();
        } catch (final ParserConfigurationException e) {
            throw ProcessException.wrap(e);
        }
        return parser.getXMLReader();
    }
}
