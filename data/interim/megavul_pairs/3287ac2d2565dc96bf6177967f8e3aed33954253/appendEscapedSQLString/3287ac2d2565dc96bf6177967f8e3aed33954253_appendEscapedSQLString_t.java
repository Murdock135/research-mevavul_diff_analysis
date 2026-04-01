class appendEscapedSQLString {
public static void appendEscapedSQLString(StringBuilder sb, String sqlString) {
        sb.append('\'');
        int length = sqlString.length();
        for (int i = 0; i < length; i++) {
            char c = sqlString.charAt(i);
            if (Character.isHighSurrogate(c)) {
                if (i == length - 1) {
                    continue;
                }
                if (Character.isLowSurrogate(sqlString.charAt(i + 1))) {
                    // add them both
                    sb.append(c);
                    sb.append(sqlString.charAt(i + 1));
                    continue;
                } else {
                    // this is a lone surrogate, skip it
                    continue;
                }
            }
            if (Character.isLowSurrogate(c)) {
                continue;
            }
            if (c == '\'') {
                sb.append('\'');
            }
            sb.append(c);
        }
        sb.append('\'');
    }
}
