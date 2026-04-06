class lookupUser {
private SearchResult lookupUser(String accountName) throws NamingException {
        InitialDirContext context = initContext();

        String searchString = searchFilter.replace(":login", accountName);

        SearchControls searchControls = new SearchControls();
        String[] attributeFilter = {idAttribute, nameAttribute, mailAttribute};
        searchControls.setReturningAttributes(attributeFilter);
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        NamingEnumeration<SearchResult> results = context.search(searchBase, searchString, searchControls);

        SearchResult searchResult = null;
        if (results.hasMoreElements()) {
            searchResult = results.nextElement();
            if (results.hasMoreElements()) {
                LOGGER.warn("Matched multiple users for the accountName: " + accountName);
                return null;
            }
        }

        return searchResult;
    }
}
