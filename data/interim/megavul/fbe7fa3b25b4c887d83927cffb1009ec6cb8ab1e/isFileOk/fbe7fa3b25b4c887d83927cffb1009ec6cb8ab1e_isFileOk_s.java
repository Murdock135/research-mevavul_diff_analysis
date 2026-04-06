class isFileOk {
private boolean isFileOk() {
		// ::comment when __CORE__
		if (SecurityUtils.getSecurityProfile() == SecurityProfile.SANDBOX)
			// In SANDBOX, we cannot read any files
			return false;

		// In any case SFile should not access the security folders
		// (the files must be handled internally)
		try {
			if (isDenied())
				return false;
		} catch (IOException e) {
			return false;
		}
		// Files in "plantuml.include.path" and "plantuml.allowlist.path" are ok.
		if (isInAllowList(SecurityUtils.getPath(SecurityUtils.PATHS_INCLUDES)))
			return true;

		if (isInAllowList(SecurityUtils.getPath(SecurityUtils.ALLOWLIST_LOCAL_PATHS)))
			return true;

		if (SecurityUtils.getSecurityProfile() == SecurityProfile.INTERNET)
			return false;

		if (SecurityUtils.getSecurityProfile() == SecurityProfile.ALLOWLIST)
			return false;

		if (SecurityUtils.getSecurityProfile() != SecurityProfile.UNSECURE) {
			// For UNSECURE, we did not do those checks
			final String path = getCleanPathSecure();
			if (path.startsWith("/etc/") || path.startsWith("/dev/") || path.startsWith("/boot/")
					|| path.startsWith("/proc/") || path.startsWith("/sys/"))
				return false;

			if (path.startsWith("//"))
				return false;

		}
		// ::done
		return true;
	}
}
