class path {
public HttpRequest path(String path) {
		// this must be the only place that sets the path

		if (!path.startsWith(StringPool.SLASH)) {
			path = StringPool.SLASH + path;
		}

		// remove fragment
		final int fragmentIndex = path.indexOf('#');
		if (path.indexOf('#') != -1) {
			this.fragment = URLCoder.encodePath(path.substring(fragmentIndex + 1), StandardCharsets.UTF_8);
			path = path.substring(0, fragmentIndex);
		}

		final int ndx = path.indexOf('?');

		if (ndx != -1) {
			final String queryString = path.substring(ndx + 1);

			path = URLCoder.encodePath(path.substring(0, ndx), StandardCharsets.UTF_8);

			query = HttpUtil.parseQuery(queryString, true);
		} else {
			query = HttpMultiMap.newCaseInsensitiveMap();
		}

		this.path = URLCoder.encodePath(path, StandardCharsets.UTF_8);

		return this;
	}
}
