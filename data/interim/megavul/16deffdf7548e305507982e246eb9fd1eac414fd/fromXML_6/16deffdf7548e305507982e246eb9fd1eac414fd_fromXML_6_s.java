class fromXML_6 {
public static CertEnrollmentRequest fromXML(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xml)));

        Element profileElement = document.getDocumentElement();
        return fromDOM(profileElement);
    }
}
