class parse {
public static NSObject parse(final byte[] bytes) throws ParserConfigurationException, ParseException, SAXException, PropertyListFormatException, IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        return parse(bis);
    }
}
