class ldapAuthenticationGenerator {
private static void ldapAuthenticationGenerator(XmlGenerator gen, LdapAuthenticationConfig c) {
        if (c == null) {
            return;
        }
        addClusterLoginElements(gen.open("ldap"), c)
                .node("url", c.getUrl())
                .nodeIfContents("socket-factory-class-name", c.getSocketFactoryClassName())
                .nodeIfContents("parse-dn", c.getParseDn())
                .nodeIfContents("role-context", c.getRoleContext())
                .nodeIfContents("role-filter", c.getRoleFilter())
                .nodeIfContents("role-mapping-attribute", c.getRoleMappingAttribute())
                .nodeIfContents("role-mapping-mode", c.getRoleMappingMode())
                .nodeIfContents("role-name-attribute", c.getRoleNameAttribute())
                .nodeIfContents("role-recursion-max-depth", c.getRoleRecursionMaxDepth())
                .nodeIfContents("role-search-scope", c.getRoleSearchScope())
                .nodeIfContents("user-name-attribute", c.getUserNameAttribute())
                .nodeIfContents("system-user-dn", c.getSystemUserDn())
                .nodeIfContents("system-user-password", c.getSystemUserPassword())
                .nodeIfContents("system-authentication", c.getSystemAuthentication())
                .nodeIfContents("security-realm", c.getSecurityRealm())
                .nodeIfContents("password-attribute", c.getPasswordAttribute())
                .nodeIfContents("user-context", c.getUserContext())
                .nodeIfContents("user-filter", c.getUserFilter())
                .nodeIfContents("user-search-scope", c.getUserSearchScope())
                .nodeIfContents("skip-authentication", c.getSkipAuthentication())
                .close();
    }
}
