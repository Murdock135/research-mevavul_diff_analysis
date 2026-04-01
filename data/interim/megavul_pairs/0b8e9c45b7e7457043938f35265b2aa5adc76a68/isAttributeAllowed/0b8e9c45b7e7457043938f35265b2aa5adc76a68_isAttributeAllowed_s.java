class isAttributeAllowed {
@Override
    public boolean isAttributeAllowed(String elementName, String attributeName, String attributeValue)
    {
        boolean result = false;

        String lowerElement = elementName.toLowerCase();
        String lowerAttribute = attributeName.toLowerCase();

        if ((DATA_ATTR.matcher(lowerAttribute).find() || ARIA_ATTR.matcher(lowerAttribute).find())
            && !this.forbidAttributes.contains(lowerAttribute))
        {
            result = true;
        } else if (isAttributeAllowed(lowerAttribute) && !this.forbidAttributes.contains(lowerAttribute)) {
            result = isAllowedValue(lowerElement, lowerAttribute, attributeValue);
        }

        return result;
    }
}
