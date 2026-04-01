class getAttributeValue {
public static String getAttributeValue(Attribute attribute) {
        String str = trim(attribute.getValue());
        str = StringUtil.getSystemPropertyAsString(str);
        return str;
    }
}
