class fromXML_24 {
public static ProfileInput fromXML(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xml)));

        Element profileElement = document.getDocumentElement();
        return fromDOM(profileElement);
    }
}
