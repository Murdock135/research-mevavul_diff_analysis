class parseProperties_1 {
private Map < String, Property<?>> parseProperties(String prefix, Map<String, String> mapConfigProperties) {
        Map < String, Property<?>> result = new HashMap<>();
        int idx = 0;
        String currentPropertyKey = prefix  + "." + idx;
        while (mapConfigProperties.containsKey(currentPropertyKey +  "." + PROPERTY_PARAMNAME)) {
            
            assertKeyNotEmpty(mapConfigProperties, currentPropertyKey +  "." + PROPERTY_PARAMNAME);
            String name     = mapConfigProperties.get(currentPropertyKey +  "." + PROPERTY_PARAMNAME);
            
            assertKeyNotEmpty(mapConfigProperties, currentPropertyKey +  "." + PROPERTY_PARAMVALUE);
            String strValue = mapConfigProperties.get(currentPropertyKey +  "." + PROPERTY_PARAMVALUE);
            
            Property<?> ap      = new PropertyString(name, strValue);
            String optionalType = mapConfigProperties.get(currentPropertyKey + "." + PROPERTY_PARAMTYPE);
            // If specific type defined ?
            if (null != optionalType) {
                // Substitution if relevant (e.g. 'int' -> 'org.ff4j.property.PropertyInt')
                optionalType = MappingUtil.mapPropertyType(optionalType);
                try {
                    // Constructor (String, String) is mandatory in Property interface
                    Class<?> typeClass = Class.forName(optionalType);
                    if (!Property.class.isAssignableFrom(typeClass)) {
                        throw new IllegalArgumentException("Cannot create property <" + name + "> invalid type <" + optionalType + ">");
                    }
                    Constructor<?> constr = typeClass.getConstructor(String.class, String.class);
                    ap = (Property<?>) constr.newInstance(name, strValue);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Cannot instantiate '" + optionalType + "' check default constructor", e);
                }
            }
            // Description
            String description = mapConfigProperties.get(currentPropertyKey + "." + PROPERTY_PARAMDESCRIPTION);
            if (null != description) {
                ap.setDescription(description);
            } 
            // Fixed Values
            String strFixedValues = mapConfigProperties.get(currentPropertyKey + "." + PROPERTY_PARAMFIXED_VALUES);
            if (null != strFixedValues && !"".equals(strFixedValues)) {
                Arrays.asList(strValue.split(","))
                      .stream()
                      .map(String::trim)
                      .forEach(ap::add2FixedValueFromString);
            }
          
            // Check fixed value
            if (ap.getFixedValues() != null &&  
               !ap.getFixedValues().isEmpty() && 
               !ap.getFixedValues().contains(ap.getValue())) {
                throw new IllegalArgumentException("Cannot create property <" + ap.getName() + 
                        "> invalid value <" + ap.getValue() + 
                        "> expected one of " + ap.getFixedValues());
            }
            result.put(ap.getName(), ap);
            // ff4j.properties.X
            currentPropertyKey = prefix + "." + ++idx;
        }
        return result;
    }
}
