class toXMLName {
public static String toXMLName(final String name) {
        final StringBuilder builder = new StringBuilder();
        boolean firstWord = true;
        for (int i = 0; i < name.length(); i++) {
            final char c = name.charAt(i);

            if (i == 0) {
                builder.append(Character.toLowerCase(c));
            } else {
                if (firstWord) {
                    if (i + 2 < name.length() && Character.isLowerCase(name.charAt(i + 2))) {
                        firstWord = false;
                    }
                    builder.append(Character.toLowerCase(c));
                } else {
                    builder.append(c);
                }
            }
        }

        return builder.toString();
    }
}
