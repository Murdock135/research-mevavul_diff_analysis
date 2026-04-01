class parse_2 {
public static NSObject parse(InputStream is) throws ParserConfigurationException, IOException, SAXException, PropertyListFormatException, ParseException {
        DocumentBuilder docBuilder = getDocBuilder();

        Document doc = docBuilder.parse(is);

        return parse(doc);
    }
}
