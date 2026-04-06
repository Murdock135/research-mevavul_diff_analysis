class parseProperties {
@SuppressWarnings("unchecked")
    private Map < String, Property<?>> parseProperties(List<Map<String, Object>> properties) {
        Map < String, Property<?>> result = new HashMap<>();
        if (null != properties) {
            properties.forEach(property -> {
                // Initiate with name and value
                String name     = (String) property.get(PROPERTY_PARAMNAME);
                if (null == name) { 
                    throw new IllegalArgumentException("Invalid YAML File: 'name' is expected for properties");
                }
                
                Object objValue = property.get(PROPERTY_PARAMVALUE);
                if (null == objValue) {
                    throw new IllegalArgumentException("Invalid YAML File: 'value' is expected for properties");
                }
                // Convert as a String
                String strValue = String.valueOf(objValue);
                if (objValue instanceof Date) {
                    strValue = SIMPLE_DATE_FORMAT.format((Date) objValue);
                }
                
                Property<?> ap = new PropertyString(name, strValue);
                String optionalType = (String) property.get(PROPERTY_PARAMTYPE);
                // If specific type defined ?
                if (null != optionalType) {
                    // Substitution if relevant (e.g. 'int' -> 'org.ff4j.property.PropertyInt')
                    optionalType = MappingUtil.mapPropertyType(optionalType);
                    try {
                        // Constructor (String, String) is mandatory in Property interface
                        Constructor<?> constr = Class.forName(optionalType).getConstructor(String.class, String.class);
                        ap = (Property<?>) constr.newInstance(name, strValue);
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Cannot instantiate '" + optionalType + "' check default constructor", e);
                    }
                }
                // Description
                String description = (String) property.get(PROPERTY_PARAMDESCRIPTION);
                if (null != description) {
                    ap.setDescription(description);
                } 
                // Fixed Values
                List<Object> fixedValues = (List<Object>) property.get(PROPERTY_PARAMFIXED_VALUES);
                if (null != fixedValues && fixedValues.size() > 0) {
                    fixedValues.stream().map(Object::toString).forEach(ap::add2FixedValueFromString);
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
            });
        }
        return result;
    }
}
