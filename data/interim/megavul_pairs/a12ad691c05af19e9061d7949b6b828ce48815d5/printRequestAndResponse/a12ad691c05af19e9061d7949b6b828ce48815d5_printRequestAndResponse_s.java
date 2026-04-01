class printRequestAndResponse {
private void printRequestAndResponse(Source sourceResponse, boolean buildResponseDocumentEnvelope,
            boolean buildResponseDocumentBody,
            Document responseDocumentEnvelope, Document responseDocumentBody) {
        try {
            getTransformer().transform(sourceResponse, new StreamResult(System.err));
            if (buildResponseDocumentEnvelope) {
                getTransformer().transform(new DOMSource(responseDocumentEnvelope), new StreamResult(System.err));
            } else if (buildResponseDocumentBody) {
                getTransformer().transform(new DOMSource(responseDocumentEnvelope), new StreamResult(System.err));
                getTransformer().transform(new DOMSource(responseDocumentBody), new StreamResult(System.err));
            }
        } catch (final TransformerException e) {
            e.printStackTrace();
        }
    }
}
