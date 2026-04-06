class sanitizeString {
public static String sanitizeString(String raw, boolean allowHTML)
    {
        if (raw==null || raw.length()==0) {
            return raw;
        }

        Matcher scriptMatcher = scriptPattern.matcher(raw);
        String next = scriptMatcher.replaceAll("&#x73;cript");
        if (!allowHTML) {
            next = next.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&quot;");
        }
        return next;
    }
}
