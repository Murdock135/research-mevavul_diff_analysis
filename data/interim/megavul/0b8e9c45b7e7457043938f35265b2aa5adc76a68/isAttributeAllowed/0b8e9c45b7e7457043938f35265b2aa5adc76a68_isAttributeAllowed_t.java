class isAttributeAllowed {
@Override
    public boolean isAttributeAllowed(String elementName, String attributeName, String attributeValue)
    {
        boolean result = false;

        String lowerElement = elementName.toLowerCase();
        String lowerAttribute = attributeName.toLowerCase();

        if ((DATA_ATTR.matcher(lowerAttribute).matches() || ARIA_ATTR.matcher(lowerAttribute).matches())
            && !this.forbidAttributes.contains(lowerAttribute))
        {
            result = true;
        } else if (isAttributeAllowed(lowerAttribute) && !this.forbidAttributes.contains(lowerAttribute)) {
            result = isAllowedValue(lowerElement, lowerAttribute, attributeValue);
        }

        return result;
    }
}
