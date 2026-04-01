class printXMLElement_2 {
@Override
    public void printXMLElement(String name, Map<String, String> attributes)
    {
        if (this.htmlElementSanitizer == null || this.htmlElementSanitizer.isElementAllowed(name)) {
            handleSpaceWhenStartElement();
            super.printXMLElement(name, cleanAttributes(name, attributes));
        }
    }
}
