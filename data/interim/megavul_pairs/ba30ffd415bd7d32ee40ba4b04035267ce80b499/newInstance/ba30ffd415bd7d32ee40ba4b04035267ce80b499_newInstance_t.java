class newInstance {
public static TransformerFactory newInstance() {
        final TransformerFactory factory = TransformerFactory.newInstance();
        secureProcessing(factory);
        return factory;
    }
}
