class isAdmin {
private boolean isAdmin(String accountName) {
        if (this.adminFilter != null) {
            try {
                InitialDirContext context = initContext();
                String searchString = adminFilter.replace(":login", encodeForLdap(accountName));
                SearchControls searchControls = new SearchControls();
                searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
                NamingEnumeration<SearchResult> results = context.search(searchBase, searchString, searchControls);
                if (results.hasMoreElements()) {
                    results.nextElement();
                    if (results.hasMoreElements()) {
                        LOGGER.warn("Matched multiple users for the accountName: " + accountName);
                        return false;
                    }
                    return true;
                }
            } catch (NamingException e) {
                return false;
            }
        }
        return false;
    }
}
