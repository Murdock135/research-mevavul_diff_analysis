class printXMLElement_1 {
@Override
    public void printXMLElement(String name)
    {
        if (this.htmlElementSanitizer == null || this.htmlElementSanitizer.isElementAllowed(name)) {
            handleSpaceWhenStartElement();
            super.printXMLElement(name);
        }
    }
}
