class setResult {
@SuppressWarnings("unchecked")
    @Override
    public <T extends Result> T setResult(Class<T> resultClass) throws SQLException {
        try {
            if (isDebugEnabled()) {
                debugCode(
                        "getSource(" + (resultClass != null ? resultClass.getSimpleName() + ".class" : "null") + ')');
            }
            checkEditable();
            if (resultClass == null || resultClass == DOMResult.class) {
                domResult = new DOMResult();
                state = State.SET_CALLED;
                return (T) domResult;
            } else if (resultClass == SAXResult.class) {
                SAXTransformerFactory transformerFactory = (SAXTransformerFactory) TransformerFactory.newInstance();
                TransformerHandler transformerHandler = transformerFactory.newTransformerHandler();
                Writer writer = setCharacterStreamImpl();
                transformerHandler.setResult(new StreamResult(writer));
                SAXResult saxResult = new SAXResult(transformerHandler);
                closable = writer;
                state = State.SET_CALLED;
                return (T) saxResult;
            } else if (resultClass == StAXResult.class) {
                XMLOutputFactory xof = XMLOutputFactory.newInstance();
                Writer writer = setCharacterStreamImpl();
                StAXResult staxResult = new StAXResult(xof.createXMLStreamWriter(writer));
                closable = writer;
                state = State.SET_CALLED;
                return (T) staxResult;
            } else if (StreamResult.class.equals(resultClass)) {
                Writer writer = setCharacterStreamImpl();
                StreamResult streamResult = new StreamResult(writer);
                closable = writer;
                state = State.SET_CALLED;
                return (T) streamResult;
            }
            throw unsupported(resultClass.getName());
        } catch (Exception e) {
            throw logAndConvert(e);
        }
    }
}
