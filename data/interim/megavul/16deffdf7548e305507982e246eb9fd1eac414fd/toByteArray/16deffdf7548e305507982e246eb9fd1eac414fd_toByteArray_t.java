class toByteArray {
public byte[] toByteArray() throws TransformerConfigurationException, TransformerException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        TransformerFactory tranFactory = TransformerFactory.newInstance();
        tranFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        tranFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        Transformer aTransformer = tranFactory.newTransformer();
        Source src = new DOMSource(mDoc);
        Result dest = new StreamResult(bos);
        aTransformer.transform(src, dest);
        return bos.toByteArray();
    }
}
