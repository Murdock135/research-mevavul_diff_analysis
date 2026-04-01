class setCommonOutputProperties {
public static void setCommonOutputProperties(final Transformer transformer, final boolean indentOutput) {
        transformer.setOutputProperty(OutputKeys.METHOD, XML);
        transformer.setOutputProperty(OutputKeys.ENCODING, UTF_8);
        transformer.setOutputProperty(OutputKeys.VERSION, VERSION);
        if (indentOutput) {
            transformer.setOutputProperty(OutputKeys.INDENT, YES);
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
        } else {
            transformer.setOutputProperty(OutputKeys.INDENT, NO);
        }
    }
}
