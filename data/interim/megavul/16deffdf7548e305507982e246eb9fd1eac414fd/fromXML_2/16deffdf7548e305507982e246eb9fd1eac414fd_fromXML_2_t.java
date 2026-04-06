class fromXML_2 {
public static PolicyConstraintValue fromXML(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xml)));

        Element pcvElement = document.getDocumentElement();
        return fromDOM(pcvElement);
    }
}
