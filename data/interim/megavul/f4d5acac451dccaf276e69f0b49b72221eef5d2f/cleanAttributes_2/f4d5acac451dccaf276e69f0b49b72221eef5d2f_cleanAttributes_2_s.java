class cleanAttributes_2 {
private Map<String, String> cleanAttributes(String elementName, Map<String, String> attributes)
    {
        Map<String, String> cleanAttributes;

        if (this.htmlElementSanitizer == null || attributes == null) {
            cleanAttributes = attributes;
        } else {
            cleanAttributes = new LinkedHashMap<>();
            for (Map.Entry<String, String> e : attributes.entrySet()) {
                if (this.htmlElementSanitizer.isAttributeAllowed(elementName, e.getKey(), e.getValue())) {
                    cleanAttributes.put(e.getKey(), e.getValue());
                } else {
                    cleanAttributes.put(TRANSLATED_ATTRIBUTE_PREFIX + e.getKey(), e.getValue());
                }
            }
        }

        return cleanAttributes;
    }
}
