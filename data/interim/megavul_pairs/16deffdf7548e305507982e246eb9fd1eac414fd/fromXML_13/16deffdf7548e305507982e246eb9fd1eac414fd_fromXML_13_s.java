class fromXML_13 {
public static CertRevokeRequest fromXML(String xml) throws Exception {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xml)));

        Element requestElement = document.getDocumentElement();
        return fromDOM(requestElement);
    }
}
