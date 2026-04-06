class restoreFromXml {
public static PersistableBundle restoreFromXml(TypedXmlPullParser in) throws IOException,
            XmlPullParserException {
        final int outerDepth = in.getDepth();
        final String startTag = in.getName();
        final String[] tagName = new String[1];
        int event;
        while (((event = in.next()) != XmlPullParser.END_DOCUMENT) &&
                (event != XmlPullParser.END_TAG || in.getDepth() < outerDepth)) {
            if (event == XmlPullParser.START_TAG) {
                // Don't throw an exception when restoring from XML since an attacker could try to
                // input invalid data in the persisted file.
                return new PersistableBundle((ArrayMap<String, Object>)
                        XmlUtils.readThisArrayMapXml(in, startTag, tagName,
                        new MyReadMapCallback()),
                        /* throwException */ false);
            }
        }
        return new PersistableBundle();  // An empty mutable PersistableBundle
    }
}
