class create_2 {
public static XMLBuilder2 create(String name, String namespaceURI)
    {
        try {
            return new XMLBuilder2(createDocumentImpl(name, namespaceURI));
        } catch (ParserConfigurationException e) {
            throw wrapExceptionAsRuntimeException(e);
        }
    }
}
