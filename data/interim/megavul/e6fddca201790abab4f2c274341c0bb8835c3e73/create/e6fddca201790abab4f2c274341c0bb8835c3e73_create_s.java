class create {
public static XMLBuilder create(String name, String namespaceURI)
        throws ParserConfigurationException, FactoryConfigurationError
    {
        return new XMLBuilder(createDocumentImpl(name, namespaceURI));
    }
}
