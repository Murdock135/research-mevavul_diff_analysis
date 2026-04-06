class parseAutodiscover {
private static HostAuth parseAutodiscover(final EasResponse resp) {
        // The response to Autodiscover is regular XML (not WBXML)
        try {
            final XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(resp.getInputStream(), "UTF-8");
            if (parser.getEventType() != XmlPullParser.START_DOCUMENT) {
                return null;
            }
            if (parser.next() != XmlPullParser.START_TAG) {
                return null;
            }
            if (!parser.getName().equals(ELEMENT_NAME_AUTODISCOVER)) {
                return null;
            }

            final HostAuth hostAuth = new HostAuth();
            while (true) {
                final int type = parser.nextTag();
                if (type == XmlPullParser.END_TAG && parser.getName()
                        .equals(ELEMENT_NAME_AUTODISCOVER)) {
                    break;
                } else if (type == XmlPullParser.START_TAG && parser.getName()
                        .equals(ELEMENT_NAME_RESPONSE)) {
                    parseResponse(parser, hostAuth);
                    // Valid responses will set the address.
                    if (hostAuth.mAddress != null) {
                        return hostAuth;
                    }
                }
            }
        } catch (final XmlPullParserException e) {
            // Parse error.
        } catch (final IOException e) {
            // Error reading parser.
        }
        return null;
    }
}
