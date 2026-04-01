class retrieveNamedSlow {
public static synchronized LicenseInfo retrieveNamedSlow() {
		cache = LicenseInfo.NONE;
		if (OptionFlags.ALLOW_INCLUDE == false)
			return cache;

		final String key = prefs.get("license", "");
		if (key.length() > 0) {
			cache = setIfValid(retrieveNamed(key), cache);
			if (cache.isValid())
				return cache;

		}
		for (SFile f : fileCandidates()) {
			try {
				if (f.exists() && f.canRead()) {
					final LicenseInfo result = retrieve(f);
					if (result == null)
						return null;

					cache = setIfValid(result, cache);
					if (cache.isValid())
						return cache;

				}
			} catch (IOException e) {
				Log.info("Error " + e);
				// Logme.error(e);
			}
		}
		return cache;
	}
}
