class parse {
public static XMLBuilder parse(String xmlString)
        throws ParserConfigurationException, SAXException, IOException
    {
        return XMLBuilder.parse(new InputSource(new StringReader(xmlString)));
    }
}
