class parseFlipStrategy_1 {
private FlippingStrategy parseFlipStrategy(Element flipStrategyTag, String uid) {
        NamedNodeMap nnm = flipStrategyTag.getAttributes();
        FlippingStrategy flipStrategy;
        if (nnm.getNamedItem(FLIPSTRATEGY_ATTCLASS) == null) {
            throw new IllegalArgumentException("Error syntax in configuration file : '" + FLIPSTRATEGY_ATTCLASS
                    + "' is required for each flipstrategy (feature=" + uid + ")");
        }

        try {
            // Attribute CLASS
            String clazzName = nnm.getNamedItem(FLIPSTRATEGY_ATTCLASS).getNodeValue();
            Class<?> typeClass = Class.forName(clazzName);
            if (!FlippingStrategy.class.isAssignableFrom(typeClass)) {
                throw new IllegalArgumentException("Cannot create flipstrategy <" + clazzName + "> invalid type");
            }
            flipStrategy = (FlippingStrategy) typeClass.newInstance();

            // LIST OF PARAMS
            Map<String, String> parameters = new LinkedHashMap<String, String>();
            NodeList initparamsNodes = flipStrategyTag.getElementsByTagName(FLIPSTRATEGY_PARAMTAG);
            for (int k = 0; k < initparamsNodes.getLength(); k++) {
                Element param = (Element) initparamsNodes.item(k);
                NamedNodeMap nnmap = param.getAttributes();
                // Check for required attribute name
                String currentParamName;
                if (nnmap.getNamedItem(FLIPSTRATEGY_PARAMNAME) == null) {
                    throw new IllegalArgumentException(ERROR_SYNTAX_IN_CONFIGURATION_FILE
                            + "'name' is required for each param in flipstrategy(check " + uid + ")");
                }
                currentParamName = nnmap.getNamedItem(FLIPSTRATEGY_PARAMNAME).getNodeValue();
                // Check for value attribute
                if (nnmap.getNamedItem(FLIPSTRATEGY_PARAMVALUE) != null) {
                    parameters.put(currentParamName, unEscapeXML(
                            nnmap.getNamedItem(FLIPSTRATEGY_PARAMVALUE)
                                 .getNodeValue()));
                } else if (param.getFirstChild() != null) {
                    parameters.put(currentParamName, unEscapeXML(
                            param.getFirstChild().getNodeValue()));
                } else {
                    throw new IllegalArgumentException("Parameter '" + currentParamName + "' in feature '" + uid
                            + "' has no value, please check XML");
                }
            }

            flipStrategy.init(uid, parameters);
        } catch (Exception e) {
            throw new IllegalArgumentException("An error occurs during flipstrategy parsing TAG" + uid, e);
        }
        return flipStrategy;
    }
}
