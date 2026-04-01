class getenv {
private String getenv(String name) {
		// Check, if the script requests secret information.
		// A plantuml server should have an own SecurityManager to
		// avoid access to properties and environment variables, but we should
		// also stop here in other deployments.
		if (SecurityUtils.isSecurityEnv(name))
			return null;
		final String env = System.getProperty(name);
		if (env != null)
			return env;

		return System.getenv(name);
	}
}
