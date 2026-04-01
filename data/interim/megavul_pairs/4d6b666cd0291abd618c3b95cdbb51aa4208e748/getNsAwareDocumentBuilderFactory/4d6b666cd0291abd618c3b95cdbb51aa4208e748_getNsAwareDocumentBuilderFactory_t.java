class getNsAwareDocumentBuilderFactory {
public static DocumentBuilderFactory getNsAwareDocumentBuilderFactory() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        setFeature(dbf, FEATURES_DISALLOW_DOCTYPE);
        return dbf;
    }
}
