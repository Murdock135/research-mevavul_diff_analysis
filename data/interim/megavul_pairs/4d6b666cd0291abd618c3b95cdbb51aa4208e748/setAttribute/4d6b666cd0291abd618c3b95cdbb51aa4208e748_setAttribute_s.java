class setAttribute {
static void setAttribute(TransformerFactory transformerFactory, String attributeName) {
        try {
            transformerFactory.setAttribute(attributeName, "");
        } catch (IllegalArgumentException iae) {
            if (Boolean.getBoolean(SYSTEM_PROPERTY_IGNORE_XXE_PROTECTION_FAILURES)) {
                LOGGER.warning("Enabling XXE protection failed. The attribute " + attributeName
                        + " is not supported by the TransformerFactory. The " + SYSTEM_PROPERTY_IGNORE_XXE_PROTECTION_FAILURES
                        + " system property is used so the XML processing continues in the UNSECURE mode"
                        + " with XXE protection disabled!!!");
            } else {
                LOGGER.severe("Enabling XXE protection failed. The attribute " + attributeName
                        + " is not supported by the TransformerFactory. This usually mean an outdated XML processor"
                        + " is present on the classpath (e.g. Xerces, Xalan). If you are not able to resolve the issue by"
                        + " fixing the classpath, the " + SYSTEM_PROPERTY_IGNORE_XXE_PROTECTION_FAILURES
                        + " system property can be used to disable XML External Entity protections."
                        + " We don't recommend disabling the XXE as such the XML processor configuration is unsecure!!!", iae);
                throw iae;
            }
        }
    }
}
