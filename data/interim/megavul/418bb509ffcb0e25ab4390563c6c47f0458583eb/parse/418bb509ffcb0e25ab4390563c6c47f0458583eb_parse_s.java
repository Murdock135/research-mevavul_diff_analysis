class parse {
private void parse(UserRequest ureq) {
		String[] sFiles = ureq.getHttpReq().getParameterValues(FORM_ID);
		if (sFiles == null || sFiles.length == 0) return;
		files = Arrays.asList(sFiles);
	}
}
