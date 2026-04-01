class toXMLString {
public String toXMLString() throws TransformerConfigurationException, TransformerException {
        TransformerFactory tranFactory = TransformerFactory.newInstance();
        tranFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        tranFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        Transformer transformer = tranFactory.newTransformer();
        Source src = new DOMSource(mDoc);
        StreamResult dest = new StreamResult(new StringWriter());
        transformer.transform(src, dest);
        String xmlString = dest.getWriter().toString();
        return xmlString;
    }
}
