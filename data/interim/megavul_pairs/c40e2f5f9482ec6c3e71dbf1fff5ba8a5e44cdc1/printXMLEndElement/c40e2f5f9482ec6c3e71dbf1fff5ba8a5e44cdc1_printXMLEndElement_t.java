class printXMLEndElement {
@Override
    public void printXMLEndElement(String name)
    {
        if (this.htmlElementSanitizer == null || this.htmlElementSanitizer.isElementAllowed(name)) {
            handleSpaceWhenEndlement();
            super.printXMLEndElement(name);
            this.elementEnded = true;
        }
    }
}
