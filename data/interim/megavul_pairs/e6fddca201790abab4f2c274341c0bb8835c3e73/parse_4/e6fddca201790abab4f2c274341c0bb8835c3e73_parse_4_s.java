class parse_4 {
public static XMLBuilder2 parse(File xmlFile)
    {
        try {
            return XMLBuilder2.parse(new InputSource(new FileReader(xmlFile)));
        } catch (FileNotFoundException e) {
            throw wrapExceptionAsRuntimeException(e);
        }
    }
}
