class addCommonParams {
protected IBaseResource addCommonParams(HttpServletRequest theServletRequest, final HomeRequest theRequest, final ModelMap theModel) {
		final String serverId = theRequest.getServerIdWithDefault(myConfig);
		final String serverBase = theRequest.getServerBase(theServletRequest, myConfig);
		final String serverName = theRequest.getServerName(myConfig);
		final String apiKey = theRequest.getApiKey(theServletRequest, myConfig);
		theModel.put("serverId", serverId);
		theModel.put("base", serverBase);
		theModel.put("baseName", serverName);
		theModel.put("apiKey", apiKey);
		theModel.put("resourceName", defaultString(theRequest.getResource()));
		theModel.put("encoding", theRequest.getEncoding());
		theModel.put("pretty", theRequest.getPretty());
		theModel.put("_summary", theRequest.get_summary());
		theModel.put("serverEntries", myConfig.getIdToServerName());

		return loadAndAddConf(theServletRequest, theRequest, theModel);
	}
}
