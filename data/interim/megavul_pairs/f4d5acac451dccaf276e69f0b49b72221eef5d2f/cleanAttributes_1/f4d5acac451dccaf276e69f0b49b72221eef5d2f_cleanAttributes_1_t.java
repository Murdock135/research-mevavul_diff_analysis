class cleanAttributes_1 {
private Attributes cleanAttributes(String elementName, Attributes attributes)
    {
        Attributes allowedAttribute;

        if (this.htmlElementSanitizer == null || attributes == null) {
            allowedAttribute = attributes;
        } else {
            allowedAttribute = new AttributesImpl();

            for (int i = 0; i < attributes.getLength(); ++i) {
                if (this.htmlElementSanitizer.isAttributeAllowed(elementName, attributes.getQName(i),
                    attributes.getValue(i)))
                {
                    ((AttributesImpl) allowedAttribute).addAttribute(null, null, attributes.getQName(i),
                        null, attributes.getValue(i));
                } else {
                    // Keep but clean invalid attributes with a prefix (removed during parsing) to avoid loosing them
                    // through WYSIWYG editing.
                    String translatedName =
                        TRANSLATED_ATTRIBUTE_PREFIX + removeInvalidDataAttributeCharacters(attributes.getQName(i));
                    if (this.htmlElementSanitizer.isAttributeAllowed(elementName, translatedName,
                        attributes.getValue(i)))
                    {
                        ((AttributesImpl) allowedAttribute).addAttribute(null, null,
                            translatedName, null, attributes.getValue(i));
                    }
                }
            }
        }

        return allowedAttribute;
    }
}
