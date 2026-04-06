class printXMLEndElement {
@Override
    public void printXMLEndElement(String name)
    {
        handleSpaceWhenEndlement();
        super.printXMLEndElement(name);
        this.elementEnded = true;
    }
}
