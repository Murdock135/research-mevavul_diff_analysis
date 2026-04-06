class printXMLElement {
@Override
    public void printXMLElement(String name, String[][] attributes)
    {
        handleSpaceWhenStartElement();
        super.printXMLElement(name, attributes);
    }
}
