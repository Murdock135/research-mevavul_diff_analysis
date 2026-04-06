class getTransformer {
Transformer getTransformer() throws TransformerConfigurationException {
        if (transformer == null) {
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        }
        return transformer;
    }
}
