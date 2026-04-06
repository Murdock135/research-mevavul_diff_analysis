class getNsAwareDocumentBuilderFactory {
public static DocumentBuilderFactory getNsAwareDocumentBuilderFactory() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        setFeature(dbf, "http://apache.org/xml/features/disallow-doctype-decl");
        return dbf;
    }
}
