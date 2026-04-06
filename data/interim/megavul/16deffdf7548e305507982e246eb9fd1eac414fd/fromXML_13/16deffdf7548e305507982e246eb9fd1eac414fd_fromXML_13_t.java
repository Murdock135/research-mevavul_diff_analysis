class fromXML_13 {
public static CertRevokeRequest fromXML(String xml) throws Exception {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xml)));

        Element requestElement = document.getDocumentElement();
        return fromDOM(requestElement);
    }
}
