class getAllowedRegexp3 {
private static List<Pattern> getAllowedRegexp3(Map<String, AntiSamyPattern> commonRegularExpressions1, Element ele, String name) throws PolicyException {
        List<Pattern> allowedRegExp = new ArrayList<Pattern>();
        for (Element regExpNode : getGrandChildrenByTagName(ele, "regexp-list", "regexp")) {
            String regExpName = getAttributeValue(regExpNode, "name");
            String value = getAttributeValue(regExpNode, "value");

            AntiSamyPattern pattern = commonRegularExpressions1.get(regExpName);

            if (pattern != null) {
                allowedRegExp.add(pattern.getPattern());
            } else if (value != null) {
                allowedRegExp.add(Pattern.compile(value));
            } else {
                throw new PolicyException("Regular expression '" + regExpName + "' was referenced as a common regexp in definition of '" + name + "', but does not exist in <common-regexp>");
            }
        }
        return allowedRegExp;
    }
}
