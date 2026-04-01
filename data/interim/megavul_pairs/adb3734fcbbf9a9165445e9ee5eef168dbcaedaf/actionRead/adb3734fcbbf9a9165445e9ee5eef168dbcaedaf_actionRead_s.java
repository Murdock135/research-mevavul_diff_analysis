class actionRead {
@RequestMapping(value = { "/read" })
	public String actionRead(HttpServletRequest theServletRequest, HomeRequest theRequest, BindingResult theBindingResult, ModelMap theModel) {
		addCommonParams(theServletRequest, theRequest, theModel);

		CaptureInterceptor interceptor = new CaptureInterceptor();
		GenericClient client = theRequest.newClient(theServletRequest, getContext(theRequest), myConfig, interceptor);

		RuntimeResourceDefinition def;
		try {
			def = getResourceType(theRequest, theServletRequest);
		} catch (ServletException e) {
			populateModelForResource(theServletRequest, theRequest, theModel);
			theModel.put("errorMsg", toDisplayError(e.toString(), e));
			return "resource";
		}
		String id = StringUtils.defaultString(theServletRequest.getParameter("id"));
		if (StringUtils.isBlank(id)) {
			populateModelForResource(theServletRequest, theRequest, theModel);
			theModel.put("errorMsg", toDisplayError("No ID specified", null));
			return "resource";
		}
		ResultType returnsResource = ResultType.RESOURCE;

		String versionId = StringUtils.defaultString(theServletRequest.getParameter("vid"));
		String outcomeDescription;
		if (StringUtils.isBlank(versionId)) {
			versionId = null;
			outcomeDescription = "Read Resource";
		} else {
			outcomeDescription = "VRead Resource";
		}

		long start = System.currentTimeMillis();
		try {
			IdDt resid = new IdDt(def.getName(), id, versionId);
			ourLog.info(logPrefix(theModel) + "Reading resource: {}", resid);
			if (resid.hasVersionIdPart()) {
				client.vread(def.getImplementingClass(), resid);
			} else {
				client.read(def.getImplementingClass(), resid);
			}
		} catch (Exception e) {
			returnsResource = handleClientException(client, e, theModel);
		}
		long delay = System.currentTimeMillis() - start;

		processAndAddLastClientInvocation(client, returnsResource, theModel, delay, outcomeDescription, interceptor, theRequest);

		return "result";
	}
}
