class parse {
public static NSObject parse(final byte[] bytes)
                throws ParserConfigurationException, ParseException, SAXException, PropertyListFormatException, IOException {

        return parse(new ByteArrayInputStream(bytes));
    }
}
