class printXMLStartElement_2 {
@Override
    public void printXMLStartElement(String name, Map<String, String> attributes)
    {
        handleSpaceWhenStartElement();
        super.printXMLStartElement(name, attributes);
    }
}
