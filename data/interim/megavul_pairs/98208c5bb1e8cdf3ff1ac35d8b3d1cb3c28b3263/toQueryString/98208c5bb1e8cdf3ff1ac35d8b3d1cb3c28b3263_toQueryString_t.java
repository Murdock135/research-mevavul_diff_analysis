class toQueryString {
public String toQueryString(Map<String, ?> queryParameters)
    {
        StringBuilder builder = new StringBuilder();

        if (queryParameters != null) {
            for (Map.Entry<String, ?> entry : queryParameters.entrySet()) {
                addQueryStringEntry(builder, entry.getKey(), entry.getValue());
                builder.append('&');
            }
        }

        return builder.toString();
    }
}
