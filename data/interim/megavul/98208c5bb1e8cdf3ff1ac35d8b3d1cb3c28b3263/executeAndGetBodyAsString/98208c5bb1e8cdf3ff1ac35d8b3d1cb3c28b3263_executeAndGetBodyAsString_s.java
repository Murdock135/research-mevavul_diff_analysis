class executeAndGetBodyAsString {
public String executeAndGetBodyAsString(EntityReference reference, Map<String, ?> queryParameters) throws Exception
    {
        String url = getURL(reference, "get", toQueryString(queryParameters));

        GetMethod getMethod = executeGet(url);

        String result = getMethod.getResponseBodyAsString();

        getMethod.releaseConnection();

        return result;
    }
}
