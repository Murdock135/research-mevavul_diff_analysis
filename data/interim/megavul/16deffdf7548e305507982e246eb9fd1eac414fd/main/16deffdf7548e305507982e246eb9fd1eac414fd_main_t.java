class main {
public static void main(String args[]) throws Exception {

        DomainInfo before = new DomainInfo();
        before.setName("EXAMPLE");

        SecurityDomainHost host = new SecurityDomainHost();
        host.setId("CA localhost 8443");
        host.setHostname("localhost");
        host.setPort("8080");
        host.setSecurePort("8443");
        host.setDomainManager("TRUE");

        before.addHost("CA", host);

        System.out.println("Before:");
        System.out.println(before);

        XMLObject xmlObject = convertDomainInfoToXMLObject(before);
        Document document = xmlObject.getDocument();

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        StringWriter sw = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(sw));

        System.out.println("Domain XML:");
        System.out.println(sw);

        DomainInfo after = convertXMLObjectToDomainInfo(xmlObject);

        System.out.println("After:");
        System.out.println(after);
    }
}
