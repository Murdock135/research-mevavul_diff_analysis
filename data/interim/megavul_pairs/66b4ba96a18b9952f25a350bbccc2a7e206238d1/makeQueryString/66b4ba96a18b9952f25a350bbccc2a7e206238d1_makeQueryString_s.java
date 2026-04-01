class makeQueryString {
public static String makeQueryString(final HttpServletRequest request, final Map<String,Object> additions, final String[] ignores, final IgnoreType ignoreType) {
        if (request == null || additions == null || ignores == null || ignoreType == null) {
            throw new IllegalArgumentException("Cannot take null parameters.");
        }

        final StringBuilder buffer = new StringBuilder();

        final List<String> ignoreList = Arrays.asList(ignores);

        final Enumeration<String> names = request.getParameterNames();

        while (names.hasMoreElements()) {
        	final String name = (String) names.nextElement();
        	final String[] values = request.getParameterValues(name);

            if ((ignoreType == IgnoreType.ADDITIONS_ONLY || !ignoreList.contains(name)) && values != null) {
                for (int i = 0; i < values.length; i++) {
                    buffer.append("&");
                    buffer.append(name);
                    buffer.append("=");
                    buffer.append(Util.encode(values[i]));
                }
            }
        }

        for (final Entry<String,Object> entry : additions.entrySet()) {
            final String name = entry.getKey();
            // handle both a String value or a String[] value
            final Object tmp = entry.getValue();
            final String[] values;
            if (tmp instanceof String[]) {
                values = (String[]) tmp;
            } else if (tmp instanceof String) {
                values = new String[] { (String) tmp };
            } else {
                throw new IllegalArgumentException("addition \"" + name + "\" is not of type String or String[], but is of type: " + tmp.getClass().getName());
            }

            if ((ignoreType == IgnoreType.REQUEST_ONLY || !ignoreList.contains(name)) && values != null) {
                for (int i = 0; i < values.length; i++) {
                    buffer.append("&");
                    buffer.append(name);
                    buffer.append("=");
                    buffer.append(Util.encode(values[i]));
                }
            }
        }

        // removes the first & from the buffer
        if (buffer.length() > 0 && buffer.charAt(0) == '&') {
            buffer.deleteCharAt(0);
        }

        return buffer.toString();
    }
}
