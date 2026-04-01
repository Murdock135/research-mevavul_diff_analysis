class printXMLElement {
@Override
    public void printXMLElement(String name, String[][] attributes)
    {
        if (this.htmlElementSanitizer == null || this.htmlElementSanitizer.isElementAllowed(name)) {
            handleSpaceWhenStartElement();
            super.printXMLElement(name, cleanAttributes(name, attributes));
        }
    }
}
