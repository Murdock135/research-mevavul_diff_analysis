class parsePropertiesTag {
private Map < String , Property<?>> parsePropertiesTag(Element propertiesTag) {
        Map< String , Property<?>> properties = new HashMap<String, Property<?>>(); 
        // <properties>
        NodeList lisOfProperties = propertiesTag.getElementsByTagName(PROPERTY_TAG);
        for (int k = 0; k < lisOfProperties.getLength(); k++) {
            // <property name='' value='' (type='') >
            Element propertyTag = (Element) lisOfProperties.item(k);
            NamedNodeMap attMap = propertyTag.getAttributes();
            if (attMap.getNamedItem(PROPERTY_PARAMNAME) == null) {
                throw new IllegalArgumentException("Invalid XML Syntax, "
                        + "'name' is a required attribute of 'property' TAG");
            }
            if (attMap.getNamedItem(PROPERTY_PARAMVALUE) == null) {
                throw new IllegalArgumentException("Invalid XML Syntax, "
                        + "'value' is a required attribute of 'property' TAG");
            }
            String name  = attMap.getNamedItem(PROPERTY_PARAMNAME).getNodeValue();
            String value = unEscapeXML(attMap.getNamedItem(PROPERTY_PARAMVALUE).getNodeValue());
            Property<?> ap = new PropertyString(name, value);
            
            // If specific type defined ?
            if (null != attMap.getNamedItem(PROPERTY_PARAMTYPE)) {
                String optionalType = attMap.getNamedItem(PROPERTY_PARAMTYPE).getNodeValue();
               
                // Substitution if relevant (e.g. 'int' -> 'org.ff4j.property.PropertyInt')
                optionalType = MappingUtil.mapPropertyType(optionalType);
                
                try {
                    // Constructor (String, String) is mandatory in Property interface
                    Constructor<?> constr = Class.forName(optionalType).getConstructor(String.class, String.class);
                    ap = (Property<?>) constr.newInstance(name, value);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Cannot instantiate '" + optionalType + "' check default constructor", e);
                }
            }
            
            if (null != attMap.getNamedItem(PROPERTY_PARAMDESCRIPTION)) {
                ap.setDescription(unEscapeXML(
                        attMap.getNamedItem(PROPERTY_PARAMDESCRIPTION)
                              .getNodeValue()));
            }
            
            // Is there any fixed Value ?
            NodeList listOfFixedValue = propertyTag.getElementsByTagName(PROPERTY_PARAMFIXED_VALUES);
            if (listOfFixedValue.getLength() != 0) {
                Element fixedValueTag = (Element) listOfFixedValue.item(0);
                NodeList listOfValues =  fixedValueTag.getElementsByTagName(PROPERTY_PARAMVALUE);
                for (int l = 0; l < listOfValues.getLength(); l++) {
                    Element valueTag = (Element) listOfValues.item(l);
                    ap.add2FixedValueFromString(unEscapeXML(valueTag.getTextContent()));
                }
            }
            
            // Check fixed value
            if (ap.getFixedValues() != null && !ap.getFixedValues().contains(ap.getValue())) {
                throw new IllegalArgumentException("Cannot create property <" + ap.getName() + 
                        "> invalid value <" + ap.getValue() + 
                        "> expected one of " + ap.getFixedValues());
            }
            
            properties.put(name, ap);
        }
        return properties;
    }
}
