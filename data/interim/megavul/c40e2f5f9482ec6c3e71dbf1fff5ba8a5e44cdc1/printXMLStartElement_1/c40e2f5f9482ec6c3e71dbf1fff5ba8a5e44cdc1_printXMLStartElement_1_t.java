class printXMLStartElement_1 {
@Override
    public void printXMLStartElement(String name, Attributes attributes)
    {
        if (this.htmlElementSanitizer == null || this.htmlElementSanitizer.isElementAllowed(name)) {
            handleSpaceWhenStartElement();
            super.printXMLStartElement(name, cleanAttributes(name, attributes));
        }
    }
}
