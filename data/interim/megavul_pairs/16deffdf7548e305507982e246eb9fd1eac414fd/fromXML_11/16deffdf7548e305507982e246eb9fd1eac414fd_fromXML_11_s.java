class fromXML_11 {
public static PolicyDefault fromXML(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xml)));

        Element profileParameterElement = document.getDocumentElement();
        return fromDOM(profileParameterElement);
    }
}
