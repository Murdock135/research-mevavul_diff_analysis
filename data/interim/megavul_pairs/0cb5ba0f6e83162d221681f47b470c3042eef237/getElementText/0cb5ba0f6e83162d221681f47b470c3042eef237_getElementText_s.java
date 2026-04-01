class getElementText {
public static String getElementText(XMLEventReader xmlEventReader) throws ParsingException {
        String str = null;
        try {
            str = xmlEventReader.getElementText().trim();
            str = StringUtil.getSystemPropertyAsString(str);
        } catch (XMLStreamException e) {
            throw logger.parserException(e);
        }
        return str;
    }
}
