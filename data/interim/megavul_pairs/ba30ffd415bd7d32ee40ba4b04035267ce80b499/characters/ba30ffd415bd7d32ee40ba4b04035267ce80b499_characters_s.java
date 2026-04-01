class characters {
@Override
        public void characters(final char[] ch, final int start, final int length) throws SAXException {
            content.append(ch, start, length);
        }
}
