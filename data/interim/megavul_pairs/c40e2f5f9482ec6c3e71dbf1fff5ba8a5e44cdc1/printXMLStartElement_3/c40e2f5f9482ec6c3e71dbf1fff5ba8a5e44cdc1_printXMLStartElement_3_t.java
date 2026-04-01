class printXMLStartElement_3 {
@Override
    public void printXMLStartElement(String name)
    {
        if (this.htmlElementSanitizer == null || this.htmlElementSanitizer.isElementAllowed(name)) {
            handleSpaceWhenStartElement();
            super.printXMLStartElement(name);
        }
    }
}
