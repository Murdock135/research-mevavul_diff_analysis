class getRequestPath {
@Override
	public String getRequestPath() {
		return ApplicationInfo.cutPathPrefix(request.getRequestURI());
	}
}
