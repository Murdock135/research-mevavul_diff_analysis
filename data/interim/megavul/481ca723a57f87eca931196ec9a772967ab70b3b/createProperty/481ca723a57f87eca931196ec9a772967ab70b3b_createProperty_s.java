class createProperty {
public static Property<?> createProperty(String pName, String pType, String pValue, String desc, Set < String > fixedValues) {
        Util.assertNotNull(pName);
        Util.assertNotNull(pType);
        Property<?> ap = null;
        try {
            Constructor<?> constr = Class.forName(pType).getConstructor(String.class, String.class);
            ap = (Property<?>) constr.newInstance(pName, pValue);
            ap.setDescription(desc);
            // Is there any fixed Value ?
            if (fixedValues != null && !fixedValues.isEmpty()) {
                for (String v : fixedValues) {
                    ap.add2FixedValueFromString(v.trim());
                }
                // Should be filled before test
                if (!ap.getFixedValues().contains(ap.getValue())) {
                    throw new IllegalArgumentException("Cannot create property <" + ap.getName() + "> invalid value <"
                                + ap.getValue() + "> expected one of " + ap.getFixedValues());
                }
            }
            return ap;
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot instantiate '" + pType + "' check default constructor : " + e.getMessage(), e);
        }
    }
}
