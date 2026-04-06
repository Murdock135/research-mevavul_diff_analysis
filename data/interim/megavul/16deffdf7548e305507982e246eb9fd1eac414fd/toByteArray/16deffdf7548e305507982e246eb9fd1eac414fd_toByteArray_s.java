class toByteArray {
public byte[] toByteArray() throws TransformerConfigurationException, TransformerException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        TransformerFactory tranFactory = TransformerFactory.newInstance();
        Transformer aTransformer = tranFactory.newTransformer();
        Source src = new DOMSource(mDoc);
        Result dest = new StreamResult(bos);
        aTransformer.transform(src, dest);
        return bos.toByteArray();
    }
}
