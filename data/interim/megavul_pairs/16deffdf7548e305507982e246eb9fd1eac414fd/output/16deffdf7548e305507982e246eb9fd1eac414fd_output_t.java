class output {
public void output(OutputStream os)
            throws TransformerConfigurationException, TransformerException {
        TransformerFactory tranFactory = TransformerFactory.newInstance();
        tranFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        tranFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        Transformer aTransformer = tranFactory.newTransformer();
        Source src = new DOMSource(mDoc);
        Result dest = new StreamResult(os);
        aTransformer.transform(src, dest);
    }
}
