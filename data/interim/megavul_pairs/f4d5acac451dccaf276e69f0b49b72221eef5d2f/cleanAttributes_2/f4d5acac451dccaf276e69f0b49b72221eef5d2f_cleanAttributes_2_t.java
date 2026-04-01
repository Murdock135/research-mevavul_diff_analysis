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
                    // Keep but clean invalid attributes with a prefix (removed during parsing) to avoid loosing them
                    // through WYSIWYG editing.
                    String translatedName =
                        TRANSLATED_ATTRIBUTE_PREFIX + removeInvalidDataAttributeCharacters(e.getKey());
                    if (this.htmlElementSanitizer.isAttributeAllowed(elementName, translatedName, e.getValue())) {
                        cleanAttributes.put(translatedName, e.getValue());
                    }
                }
            }
        }

        return cleanAttributes;
    }
}
