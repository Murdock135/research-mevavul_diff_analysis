class addCommonParams {
protected IBaseResource addCommonParams(HttpServletRequest theServletRequest, final HomeRequest theRequest, final ModelMap theModel) {
		final String serverId = theRequest.getServerIdWithDefault(myConfig);
		final String serverBase = theRequest.getServerBase(theServletRequest, myConfig);
		final String serverName = theRequest.getServerName(myConfig);
		final String apiKey = theRequest.getApiKey(theServletRequest, myConfig);
		theModel.put("serverId", sanitizeInput(serverId));
		theModel.put("base", sanitizeInput(serverBase));
		theModel.put("baseName", sanitizeInput(serverName));
		theModel.put("apiKey", sanitizeInput(apiKey));
		theModel.put("resourceName", sanitizeInput(defaultString(theRequest.getResource())));
		theModel.put("encoding", sanitizeInput(theRequest.getEncoding()));
		theModel.put("pretty", sanitizeInput(theRequest.getPretty()));
		theModel.put("_summary", sanitizeInput(theRequest.get_summary()));
		theModel.put("serverEntries", myConfig.getIdToServerName());

		return loadAndAddConf(theServletRequest, theRequest, theModel);
	}
}
