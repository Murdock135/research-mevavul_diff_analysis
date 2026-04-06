class output {
public void output(OutputStream os)
            throws TransformerConfigurationException, TransformerException {
        TransformerFactory tranFactory = TransformerFactory.newInstance();
        Transformer aTransformer = tranFactory.newTransformer();
        Source src = new DOMSource(mDoc);
        Result dest = new StreamResult(os);
        aTransformer.transform(src, dest);
    }
}
