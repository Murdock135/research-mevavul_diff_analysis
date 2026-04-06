class executeAndGetBodyAsString {
public String executeAndGetBodyAsString(EntityReference reference, Map<String, ?> queryParameters) throws Exception
    {
        gotoPage(getURL(reference, "get", toQueryString(queryParameters)));
        
        return getDriver().getPageSource();
    }
}
