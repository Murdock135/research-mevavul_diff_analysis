class printXMLElement_2 {
@Override
    public void printXMLElement(String name, Map<String, String> attributes)
    {
        handleSpaceWhenStartElement();
        super.printXMLElement(name, attributes);
    }
}
