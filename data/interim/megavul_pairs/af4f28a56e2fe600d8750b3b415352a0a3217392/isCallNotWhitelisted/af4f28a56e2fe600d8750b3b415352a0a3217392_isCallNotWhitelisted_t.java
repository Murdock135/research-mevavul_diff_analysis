class isCallNotWhitelisted {
private boolean isCallNotWhitelisted(String className, String methodName) {
		String call = className + "." + methodName; //$NON-NLS-1$
		return SecurityConstants.STACK_BLACKLIST.stream().anyMatch(call::startsWith)
				|| (SecurityConstants.STACK_WHITELIST.stream().noneMatch(call::startsWith)
						&& (configuration == null || !(configuration.whitelistedClassNames().contains(className)
								|| configuration.trustedPackages().stream().anyMatch(pm -> pm.matches(className)))));
	}
}
