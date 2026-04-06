class getAllowedRegexps2 {
private static List<Pattern> getAllowedRegexps2(Map<String, AntiSamyPattern> commonRegularExpressions1, Element attributeNode, String tagName) throws PolicyException {
        List<Pattern> allowedRegexps = new ArrayList<Pattern>();
        for (Element regExpNode : getGrandChildrenByTagName(attributeNode, "regexp-list", "regexp")) {
            String regExpName = getAttributeValue(regExpNode, "name");
            String value = getAttributeValue(regExpNode, "value");

            /*
            * Look up common regular expression specified
            * by the "name" field. They can put a common
            * name in the "name" field or provide a custom
            * value in the "value" field. They must choose
            * one or the other, not both.
            */
            if (regExpName != null && regExpName.length() > 0) {

                AntiSamyPattern pattern = commonRegularExpressions1.get(regExpName);
                if (pattern != null) {
                    allowedRegexps.add(pattern.getPattern());
                } else {
                    throw new PolicyException("Regular expression '" + regExpName + "' was referenced as a common regexp in definition of '" + tagName + "', but does not exist in <common-regexp>");
                }

            } else if (value != null && value.length() > 0) {
                allowedRegexps.add(Pattern.compile(value));
            }
        }
        return allowedRegexps;
    }
}
