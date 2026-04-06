class getRequestPath_1 {
@Override
	public String getRequestPath() {
		return ApplicationInfo.cutPathPrefix(request.getPath().toString());
	}
}
