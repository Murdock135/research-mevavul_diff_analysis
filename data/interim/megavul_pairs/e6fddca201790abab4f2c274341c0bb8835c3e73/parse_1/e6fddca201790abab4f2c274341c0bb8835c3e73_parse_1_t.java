class parse_1 {
public static XMLBuilder parse(File xmlFile)
        throws ParserConfigurationException, SAXException, IOException
    {
        return XMLBuilder.parse(xmlFile, false);
    }
}
