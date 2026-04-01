class quoteValue {
private String quoteValue(String val, Type type) {
        if (NumberUtils.isNumber(val) && isNumberType(type)) {
            return val;
        } else if (StringUtils.isNotBlank(val)) {
            return String.format("'%s'", StringEscapeUtils.escapeSql(val));
        }
        return "''";
    }
}
