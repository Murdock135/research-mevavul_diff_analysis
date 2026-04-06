class newSAXParser {
public SAXParser newSAXParser() {
        final SAXParser ret;
        try {
            ret = new XercesJAXPSAXParser(this, features, validating, handleXInclude);
        } catch (final SAXException se) {
            // Translate to ParserConfigurationException
            throw new OXFException(se); // so we see a decent stack trace!
        }
        return ret;
    }
}
