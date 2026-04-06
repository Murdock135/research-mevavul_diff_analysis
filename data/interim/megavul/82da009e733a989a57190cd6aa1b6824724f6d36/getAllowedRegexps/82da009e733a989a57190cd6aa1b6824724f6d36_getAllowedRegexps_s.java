class getAllowedRegexps {
private static List<Pattern> getAllowedRegexps(Map<String, AntiSamyPattern> commonRegularExpressions1, Element ele) {
        List<Pattern> allowedRegExp = new ArrayList<Pattern>();
        for (Element regExpNode : getGrandChildrenByTagName(ele, "regexp-list", "regexp")) {
            String regExpName = getAttributeValue(regExpNode, "name");
            String value = getAttributeValue(regExpNode, "value");

            if (regExpName != null && regExpName.length() > 0) {
                allowedRegExp.add(commonRegularExpressions1.get(regExpName).getPattern());
            } else {
                allowedRegExp.add(Pattern.compile(REGEXP_BEGIN + value + REGEXP_END));
            }
        }
        return allowedRegExp;
    }
}
