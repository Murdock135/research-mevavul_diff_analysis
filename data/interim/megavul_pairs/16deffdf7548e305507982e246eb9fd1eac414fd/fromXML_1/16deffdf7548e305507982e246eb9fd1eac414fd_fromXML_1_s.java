class fromXML_1 {
public static ProfileData fromXML(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xml)));

        Element profileDataElement = document.getDocumentElement();
        return fromDOM(profileDataElement);
    }
}
