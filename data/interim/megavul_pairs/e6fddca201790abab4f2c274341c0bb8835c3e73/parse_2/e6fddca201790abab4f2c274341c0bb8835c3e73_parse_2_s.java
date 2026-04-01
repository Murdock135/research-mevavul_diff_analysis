class parse_2 {
public static XMLBuilder parse(InputSource inputSource)
        throws ParserConfigurationException, SAXException, IOException
    {
        return new XMLBuilder(parseDocumentImpl(inputSource));
    }
}
