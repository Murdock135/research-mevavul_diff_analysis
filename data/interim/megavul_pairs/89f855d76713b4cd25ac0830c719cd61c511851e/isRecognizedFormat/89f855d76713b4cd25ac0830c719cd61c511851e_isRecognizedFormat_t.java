class isRecognizedFormat {
@Override
    public boolean isRecognizedFormat(BufferedReader reader) throws IOException {
        Objects.requireNonNull(reader);

        /*
            The correct behavior is to return false if it is certain that the file is
            not of the MsBib type, and true otherwise. Returning true is the safe choice
            if not certain.
         */
        Document docin;
        try {
            DocumentBuilder dbuild = makeSafeDocBuilderFactory(DocumentBuilderFactory.newInstance()).newDocumentBuilder();
            dbuild.setErrorHandler(new ErrorHandler() {

                @Override
                public void warning(SAXParseException exception) throws SAXException {
                    // ignore warnings
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    throw exception;
                }

                @Override
                public void error(SAXParseException exception) throws SAXException {
                    throw exception;
                }
            });

            docin = dbuild.parse(new InputSource(reader));
        } catch (Exception e) {
            return false;
        }
        return (docin == null) || docin.getDocumentElement().getTagName().contains("Sources");
    }
}
