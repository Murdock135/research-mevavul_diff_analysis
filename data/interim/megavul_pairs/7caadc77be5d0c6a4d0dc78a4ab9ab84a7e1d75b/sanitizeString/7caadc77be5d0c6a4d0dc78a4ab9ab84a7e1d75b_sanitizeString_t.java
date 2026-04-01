class sanitizeString {
public static String sanitizeString(String raw, boolean allowHTML)
    {
        if (raw==null || raw.length()==0) {
            return raw;
        }
        String next;

        if (allowHTML) {
            Matcher scriptMatcher = scriptPattern.matcher(raw);
            next = scriptMatcher.replaceAll("&#x73;cript");

            Matcher imgOnErrorMatcher = imgOnErrorPattern.matcher(next);
            next = imgOnErrorMatcher.replaceAll("$1&#x6f;$2");
        } else {
            next = Encode.forHtmlContent(raw);
        }
        return next;
    }
}
