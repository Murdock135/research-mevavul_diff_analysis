class getRequestPath_3 {
@Override
	public String getRequestPath() {
		return ApplicationInfo.cutPathPrefix(request.getPath().toString());
	}
}
