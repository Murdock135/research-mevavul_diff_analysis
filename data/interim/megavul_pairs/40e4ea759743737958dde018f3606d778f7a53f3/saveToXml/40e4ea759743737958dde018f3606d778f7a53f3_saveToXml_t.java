class saveToXml {
public void saveToXml(TypedXmlSerializer out) throws IOException, XmlPullParserException {
        unparcel();
        // Explicitly drop invalid types an attacker may have added before persisting.
        for (int i = mMap.size() - 1; i >= 0; --i) {
            final Object value = mMap.valueAt(i);
            if (!isValidType(value)) {
                Slog.e(TAG, "Dropping bad data before persisting: "
                        + mMap.keyAt(i) + "=" + value);
                mMap.removeAt(i);
            }
        }
        XmlUtils.writeMapXml(mMap, out, this);
    }
}
