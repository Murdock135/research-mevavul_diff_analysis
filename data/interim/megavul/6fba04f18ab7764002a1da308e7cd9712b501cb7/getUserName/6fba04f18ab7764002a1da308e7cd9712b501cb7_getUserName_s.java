class getUserName {
private String getUserName(ISessionManager sessionManager,
      String userLoginName, String userDomain)
      throws RepositoryLoginException, RepositoryException {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(userLoginName),
        "Username must not be null or empty.");

    // Construct a DN for the domain, which is used in both the query
    // and the results post-processing. Note this works for both
    // NetBIOS and DNS domains.
    LdapName domainName = toLdapName(userDomain);

    ISession session = sessionManager.getSession(docbase);
    try {
      StringBuilder queryBuff = new StringBuilder();
      queryBuff.append("select user_name, user_ldap_dn from ");
      queryBuff.append("dm_user where user_login_name = '");
      queryBuff.append(userLoginName);
      if (!domainName.isEmpty()) {
        queryBuff.append("' and user_source = 'LDAP'");
        queryBuff.append(" and LOWER(user_ldap_dn) like '%,");
        queryBuff.append(domainName);
        if (domainName.size() == 1) { // NetBIOS domain
          queryBuff.append(",%");
        }
      }
      queryBuff.append("'");

      IQuery query = clientX.getQuery();
      query.setDQL(queryBuff.toString());
      ICollection users = query.execute(session, IQuery.EXECUTE_READ_QUERY);
      try {
        // The DQL query can only confirm partial matches, and not
        // exact matches. For brevity, we loop over all the users in
        // case we want to log them, and we check domainName.isEmpty()
        // on each iteration.
        ArrayList<String> matches = new ArrayList<String>();
        while (users.next()) {
          String userLdapDn = users.getString("user_ldap_dn");
          if (domainName.isEmpty()
              || domainMatchesUser(domainName, userLdapDn)) {
            matches.add(users.getString("user_name"));
          } else if (LOGGER.isLoggable(Level.FINEST)) {
            LOGGER.finest("Rejecting non-matching domain " + domainName
                + " for user " + userLdapDn);
          }
        }
        if (matches.size() == 1) {
          return matches.get(0);
        } else {
          LOGGER.log(Level.FINER, "No users or multiple users found: {0}",
              matches);
          return null;
        }
      } finally {
        users.close();
      }
    } finally {
      sessionManager.release(session);
    }
  }
}
