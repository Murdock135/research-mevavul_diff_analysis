class printXMLStartElement_1 {
@Override
    public void printXMLStartElement(String name, Attributes attributes)
    {
        handleSpaceWhenStartElement();
        super.printXMLStartElement(name, attributes);
    }
}
