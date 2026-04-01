class buildResponseDocumentBody {
private Document buildResponseDocumentBody(Document responseDocumentEnvelope) throws ConnectorException {
        Document responseDocumentBody = null;
        if (responseDocumentEnvelope != null) {
            try {
                responseDocumentBody = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            } catch (final ParserConfigurationException pce) {
                throw new ConnectorException(pce);
            }
            final Node bodyContent = getEnvelopeBodyContent(responseDocumentEnvelope);
            final Node clonedBodyContent = bodyContent.cloneNode(true);
            responseDocumentBody.adoptNode(clonedBodyContent);
            responseDocumentBody.importNode(clonedBodyContent, true);
            responseDocumentBody.appendChild(clonedBodyContent);
        }
        return responseDocumentBody;
    }
}
