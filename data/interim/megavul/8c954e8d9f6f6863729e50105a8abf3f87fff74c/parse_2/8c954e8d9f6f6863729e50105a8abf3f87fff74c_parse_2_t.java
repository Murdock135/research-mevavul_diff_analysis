class parse_2 {
public static NSObject parse(InputStream is)
                throws ParserConfigurationException, IOException, SAXException, PropertyListFormatException, ParseException {

        return parse(getDocBuilder().parse(is));
    }
}
