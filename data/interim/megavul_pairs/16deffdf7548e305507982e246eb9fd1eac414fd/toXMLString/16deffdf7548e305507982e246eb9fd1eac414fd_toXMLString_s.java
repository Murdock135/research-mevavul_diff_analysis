class toXMLString {
public String toXMLString() throws TransformerConfigurationException, TransformerException {
        TransformerFactory tranFactory = TransformerFactory.newInstance();
        Transformer transformer = tranFactory.newTransformer();
        Source src = new DOMSource(mDoc);
        StreamResult dest = new StreamResult(new StringWriter());
        transformer.transform(src, dest);
        String xmlString = dest.getWriter().toString();
        return xmlString;
    }
}
