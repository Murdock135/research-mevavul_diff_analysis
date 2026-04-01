class maybeValidateXml {
private void maybeValidateXml(File file) {
        if (!file.getName().endsWith(".xml")) {
            return;
        }

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);

        try {
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (ParserConfigurationException e) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error configuring parser factory: " + e.getMessage()).build());
        }

        final CapturingErrorHandler errorHandler = new CapturingErrorHandler();
        try {
            final DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(errorHandler);
            builder.parse(file);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("Validation failed: " + e.getMessage()).build());
        }
    }
}
