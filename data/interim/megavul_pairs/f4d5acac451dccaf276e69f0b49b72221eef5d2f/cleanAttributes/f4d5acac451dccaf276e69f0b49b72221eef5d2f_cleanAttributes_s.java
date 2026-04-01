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
                        return new String[] { TRANSLATED_ATTRIBUTE_PREFIX + entry[0], entry[1] };
                    }
                })
                .toArray(String[][]::new);
        }

        return allowedAttributes;
    }
}
