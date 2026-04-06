class cleanAttributes {
private String[][] cleanAttributes(String elementName, String[][] attributes)
    {
        String[][] allowedAttributes;
        if (this.htmlElementSanitizer == null || attributes == null) {
            allowedAttributes = attributes;
        } else {
            allowedAttributes = Arrays.stream(attributes)
                .map(entry -> {
                    if (this.htmlElementSanitizer.isAttributeAllowed(elementName, entry[0], entry[1])) {
                        return entry;
                    } else {
                        // Keep but clean invalid attributes with a prefix (removed during parsing) to avoid loosing
                        // them through WYSIWYG editing.
                        String translatedName =
                            TRANSLATED_ATTRIBUTE_PREFIX + removeInvalidDataAttributeCharacters(entry[0]);
                        if (this.htmlElementSanitizer.isAttributeAllowed(elementName, translatedName, entry[1])) {
                            return new String[] { translatedName, entry[1] };
                        } else {
                            return null;
                        }
                    }
                })
                .filter(Objects::nonNull)
                .toArray(String[][]::new);
        }

        return allowedAttributes;
    }
}
