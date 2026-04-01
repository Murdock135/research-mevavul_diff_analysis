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
            // According to https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html
            if (sourceClass == null || sourceClass == DOMSource.class) {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                for (Map.Entry<String,Boolean> entry : secureFeatureMap.entrySet()) {
                    try {
                        dbf.setFeature(entry.getKey(), entry.getValue());
                    } catch (Exception ignore) {/**/}
                }
                dbf.setXIncludeAware(false);
                dbf.setExpandEntityReferences(false);
                dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
                DocumentBuilder db = dbf.newDocumentBuilder();
                db.setEntityResolver(NOOP_ENTITY_RESOLVER);
                return (T) new DOMSource(db.parse(new InputSource(value.getInputStream())));
            } else if (sourceClass == SAXSource.class) {
                XMLReader reader = XMLReaderFactory.createXMLReader();
                for (Map.Entry<String,Boolean> entry : secureFeatureMap.entrySet()) {
                    try {
                        reader.setFeature(entry.getKey(), entry.getValue());
                    } catch (Exception ignore) {/**/}
                }
                reader.setEntityResolver(NOOP_ENTITY_RESOLVER);
                return (T) new SAXSource(reader, new InputSource(value.getInputStream()));
            } else if (sourceClass == StAXSource.class) {
                XMLInputFactory xif = XMLInputFactory.newInstance();
                xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
                xif.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
                xif.setProperty("javax.xml.stream.isSupportingExternalEntities", false);
                return (T) new StAXSource(xif.createXMLStreamReader(value.getInputStream()));
            } else if (sourceClass == StreamSource.class) {
                TransformerFactory tf = TransformerFactory.newInstance();
                tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
                tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
                tf.setURIResolver(NOOP_URI_RESOLVER);
                tf.newTransformer().transform(new StreamSource(value.getInputStream()), new SAXResult(new DefaultHandler()));
                return (T) new StreamSource(value.getInputStream());
            }
            throw unsupported(sourceClass.getName());
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }
}
