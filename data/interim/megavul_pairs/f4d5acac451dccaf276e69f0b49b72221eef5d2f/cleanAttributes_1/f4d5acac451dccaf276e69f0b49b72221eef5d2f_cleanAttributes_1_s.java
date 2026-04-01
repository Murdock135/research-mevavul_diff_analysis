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
                    ((AttributesImpl) allowedAttribute).addAttribute(null, null,
                        TRANSLATED_ATTRIBUTE_PREFIX + attributes.getQName(i), null, attributes.getValue(i));
                }
            }
        }

        return allowedAttribute;
    }
}
