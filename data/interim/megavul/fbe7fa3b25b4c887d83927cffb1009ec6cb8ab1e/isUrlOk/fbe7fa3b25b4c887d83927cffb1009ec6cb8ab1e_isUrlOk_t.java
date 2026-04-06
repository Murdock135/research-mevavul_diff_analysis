class isUrlOk {
public boolean isUrlOk() {
		// ::comment when __CORE__
		if (SecurityUtils.getSecurityProfile() == SecurityProfile.SANDBOX)
			// In SANDBOX, we cannot read any URL
			return false;

		if (SecurityUtils.getSecurityProfile() == SecurityProfile.LEGACY)
			return true;

		if (SecurityUtils.getSecurityProfile() == SecurityProfile.UNSECURE)
			// We are UNSECURE anyway
			return true;

		if (isInUrlAllowList())
			// ::done
			return true;
		// ::comment when __CORE__

		if (SecurityUtils.getSecurityProfile() == SecurityProfile.INTERNET) {
			if (forbiddenURL(cleanPath(internal.toString())))
				return false;

			final int port = internal.getPort();
			// Using INTERNET profile, port 80 and 443 are ok
			return port == 80 || port == 443 || port == -1;
		}
		return false;
		// ::done
	}
}
