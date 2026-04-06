class printXMLStartElement_2 {
@Override
    public void printXMLStartElement(String name, Map<String, String> attributes)
    {
        if (this.htmlElementSanitizer == null || this.htmlElementSanitizer.isElementAllowed(name)) {
            handleSpaceWhenStartElement();
            super.printXMLStartElement(name, cleanAttributes(name, attributes));
        }
    }
}
