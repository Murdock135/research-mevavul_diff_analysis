class saveToXml {
public void saveToXml(TypedXmlSerializer out) throws IOException, XmlPullParserException {
        unparcel();
        XmlUtils.writeMapXml(mMap, out, this);
    }
}
