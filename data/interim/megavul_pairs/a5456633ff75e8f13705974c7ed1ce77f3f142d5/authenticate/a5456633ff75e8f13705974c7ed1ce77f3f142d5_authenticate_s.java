class authenticate {
public AuthenticatedUser authenticate(String username, String password,
			String ipaddress) throws AuthenticationException {
		AuthenticatedUser user = null;
		if (username == null || username.trim().length() == 0
				|| password == null || password.trim().length() == 0) {
			throw new AuthenticationException(
					"Authentication failed: Invalid arguments");
		}
		try {

			ContentManager contentManager = ContentManager.getContentManager();
			List result = new ArrayList();
			String sql = "SELECT * FROM " + table + " WHERE "
					+ columns.get("username") + "= ? AND "
					+ columns.get("password") + "= '?'";
			
			String[] preparedVariables = new String[] {username, password};
			
			
			
			if(log.isDebugEnabled()) {
				log.debug(sql);
			}
			contentManager.doQuery(new Query(sql, preparedVariables, result));
			
			if(log.isDebugEnabled()) {
				log.debug("found " + result.size() + " records");
			}
			if(result.size() > 0) {
				// get the first found row and create user object
				Map row = (Map) result.get(0);

				// intantiate the user class an add the map
				Class clazz = Class.forName(userClass);
				if(log.isDebugEnabled()) {
					log.debug("creating user class " + clazz.getName());
				}
				DBUser dbUser = (DBUser)clazz.newInstance();
				dbUser.init(row);
				user = dbUser;
			}

		} catch (Exception e) {
			log.error("Authentication failed: Finding user failed");
			if (log.isDebugEnabled()) {
				log.debug(e.getMessage(), e);
			}
		}
		if (user == null) {
			throw new AuthenticationException(
					"Authentication failed: User not found");
		}
		return user;
	}
}
