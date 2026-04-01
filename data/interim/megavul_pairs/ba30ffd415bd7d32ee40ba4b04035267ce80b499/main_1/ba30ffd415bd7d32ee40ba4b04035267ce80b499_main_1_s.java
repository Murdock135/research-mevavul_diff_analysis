class main_1 {
public static void main(final String[] args) {
        if (args.length != 2) {
            System.out.println("Bad arguments - provide input and output XSD files.");
        }

        final Path inputXsd = Paths.get(args[0]);
        final Path outputXsd = Paths.get(args[1]);

        try (final Reader reader = Files.newBufferedReader(inputXsd, StreamUtil.DEFAULT_CHARSET);
             final Writer writer = Files.newBufferedWriter(outputXsd, StreamUtil.DEFAULT_CHARSET)) {
            final TransformerHandler th = XMLUtil.createTransformerHandler(true);
            th.setResult(new StreamResult(writer));

            SAXParser parser = null;
            try {
                parser = PARSER_FACTORY.newSAXParser();
            } catch (final ParserConfigurationException e) {
                throw ProcessException.wrap(e);
            }

            final UniqueEventFilter filter = new UniqueEventFilter();
            filter.setContentHandler(th);

            final LocationFactory locationFactory = new DefaultLocationFactory();
            final ErrorHandlerAdaptor errorHandler = new ErrorHandlerAdaptor("XMLReader", locationFactory,
                    new FatalErrorReceiver());
            final XMLReader xmlReader = parser.getXMLReader();
            xmlReader.setContentHandler(filter);
            xmlReader.setErrorHandler(errorHandler);
            xmlReader.parse(new InputSource(reader));

        } catch (final SAXException | TransformerConfigurationException | IOException e) {
            throw ProcessException.wrap(e);
        }
    }
}
