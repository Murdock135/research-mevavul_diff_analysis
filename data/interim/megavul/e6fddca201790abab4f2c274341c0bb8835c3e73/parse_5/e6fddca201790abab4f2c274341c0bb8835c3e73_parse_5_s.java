class parse_5 {
public static XMLBuilder2 parse(InputSource inputSource)
    {
        try {
            return new XMLBuilder2(parseDocumentImpl(inputSource));
        } catch (ParserConfigurationException e) {
            throw wrapExceptionAsRuntimeException(e);
        } catch (SAXException e) {
            throw wrapExceptionAsRuntimeException(e);
        } catch (IOException e) {
            throw wrapExceptionAsRuntimeException(e);
        }
    }
}
