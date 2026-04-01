class printXMLStartElement {
@Override
    public void printXMLStartElement(String name, String[][] attributes)
    {
        if (this.htmlElementSanitizer == null || this.htmlElementSanitizer.isElementAllowed(name)) {
            handleSpaceWhenStartElement();
            super.printXMLStartElement(name, cleanAttributes(name, attributes));
        }
    }
}
