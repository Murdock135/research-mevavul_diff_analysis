class create {
public static XMLBuilder create(String name, String namespaceURI)
        throws ParserConfigurationException, FactoryConfigurationError
    {
        return create(name, namespaceURI, false);
    }
}
