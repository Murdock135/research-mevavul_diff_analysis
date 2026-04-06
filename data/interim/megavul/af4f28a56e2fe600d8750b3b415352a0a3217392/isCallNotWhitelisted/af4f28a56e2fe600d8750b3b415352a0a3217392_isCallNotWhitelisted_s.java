class isCallNotWhitelisted {
private boolean isCallNotWhitelisted(String call) {
		return SecurityConstants.STACK_BLACKLIST.stream().anyMatch(call::startsWith)
				|| (SecurityConstants.STACK_WHITELIST.stream().noneMatch(call::startsWith)
						&& (configuration == null || !(configuration.whitelistedClassNames().contains(call)
								|| configuration.trustedPackages().stream().anyMatch(pm -> pm.matches(call)))));
	}
}
