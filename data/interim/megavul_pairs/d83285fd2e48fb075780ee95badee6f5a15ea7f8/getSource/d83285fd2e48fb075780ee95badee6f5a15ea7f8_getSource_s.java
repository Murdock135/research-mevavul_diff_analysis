class getSource {
@SuppressWarnings("unchecked")
    @Override
    public <T extends Source> T getSource(Class<T> sourceClass) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode(
                        "getSource(" + (sourceClass != null ? sourceClass.getSimpleName() + ".class" : "null") + ')');
            }
            checkReadable();
            if (sourceClass == null || sourceClass == DOMSource.class) {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                return (T) new DOMSource(dbf.newDocumentBuilder().parse(new InputSource(value.getInputStream())));
            } else if (sourceClass == SAXSource.class) {
                return (T) new SAXSource(new InputSource(value.getInputStream()));
            } else if (sourceClass == StAXSource.class) {
                XMLInputFactory xif = XMLInputFactory.newInstance();
                return (T) new StAXSource(xif.createXMLStreamReader(value.getInputStream()));
            } else if (sourceClass == StreamSource.class) {
                return (T) new StreamSource(value.getInputStream());
            }
            throw unsupported(sourceClass.getName());
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }
}
