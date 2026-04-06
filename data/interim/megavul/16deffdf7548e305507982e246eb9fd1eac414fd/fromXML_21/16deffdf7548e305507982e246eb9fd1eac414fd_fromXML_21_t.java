class fromXML_21 {
public static CertDataInfo fromXML(String xml) throws Exception {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xml)));

        Element infoElement = document.getDocumentElement();
        return fromDOM(infoElement);
    }
}
