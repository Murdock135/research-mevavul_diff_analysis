class setAttribute {
static void setAttribute(TransformerFactory transformerFactory, String attributeName) {
        try {
            transformerFactory.setAttribute(attributeName, "");
        } catch (IllegalArgumentException iae) {
            printWarningAndRethrowEventually(iae, TransformerFactory.class, "attribute " + attributeName);
        }
    }
}
