class getRequestPath_2 {
@Override
	public String getRequestPath() {
		return ApplicationInfo.cutPathPrefix(request.getRequestURI());
	}
}
