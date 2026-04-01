class parse_1 {
public static NSObject parse(File f)
                throws ParserConfigurationException, IOException, SAXException, PropertyListFormatException, ParseException {

        return parse(getDocBuilder().parse(new FileInputStream(f)));
    }
}
