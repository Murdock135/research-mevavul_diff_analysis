class quote {
public static String quote(String string, boolean escapeForwardSlashAlways) {
        if (string == null || string.length() == 0) {
            return "\"\"";
        }

        char         c = 0;
        int          i;
        int          len = string.length();
        StringBuilder sb = new StringBuilder(len + 4);
        String       t;

        sb.append('"');
        for (i = 0; i < len; i += 1) {
            c = string.charAt(i);
            switch (c) {
            case '\\':
                // Escape a backslash, but only if it isn't already escaped
                if (i == len - 1 || string.charAt(i + 1) != '\\') {
                    sb.append('\\');
                }
                sb.append(c);
                break;
            case '"':
                sb.append('\\');
                sb.append(c);
                break;
            case '/':
            	if (escapeForwardSlashAlways || i > 0 && string.charAt(i - 1) == '<') {
                    sb.append('\\');
            	}
                sb.append(c);
                break;
            case '\b':
                sb.append("\\b");
                break;
            case '\t':
                sb.append("\\t");
                break;
            case '\n':
                sb.append("\\n");
                break;
            case '\f':
                sb.append("\\f");
                break;
            case '\r':
                sb.append("\\r");
                break;
            case '\u2028':
                sb.append("\\u2028");
                break;
            case '\u2029':
                sb.append("\\u2029");
                break;
            default:
                if (c < ' ') {
                    t = "000" + Integer.toHexString(c);
                    sb.append("\\u" + t.substring(t.length() - 4));
                } else {
                    sb.append(c);
                }
            }
        }
        sb.append('"');
        return sb.toString();
    }
}
