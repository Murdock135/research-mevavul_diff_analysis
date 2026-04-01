class printXMLStartElement {
@Override
    public void printXMLStartElement(String name, String[][] attributes)
    {
        handleSpaceWhenStartElement();
        super.printXMLStartElement(name, attributes);
    }
}
