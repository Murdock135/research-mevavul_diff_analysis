class actionPage {
@RequestMapping(value = { "/page" })
	public String actionPage(HttpServletRequest theReq, HomeRequest theRequest, BindingResult theBindingResult, ModelMap theModel) {
		addCommonParams(theReq, theRequest, theModel);

		CaptureInterceptor interceptor = new CaptureInterceptor();
		FhirContext context = getContext(theRequest);
		GenericClient client = theRequest.newClient(theReq, context, myConfig, interceptor);

		String url = sanitizeUrlPart(defaultString(theReq.getParameter("page-url")));
		if (myConfig.isRefuseToFetchThirdPartyUrls()) {
			if (!url.startsWith(theModel.get("base").toString())) {
				ourLog.warn(logPrefix(theModel) + "Refusing to load page URL: {}", url);
				theModel.put("errorMsg", toDisplayError("Invalid page URL: " + url, null));
				return "result";
			}
		}

		url = url.replace("&amp;", "&");

		ResultType returnsResource = ResultType.BUNDLE;

		long start = System.currentTimeMillis();
		try {
			ourLog.info(logPrefix(theModel) + "Loading paging URL: {}", url);
			@SuppressWarnings("unchecked")
			Class<? extends IBaseBundle> bundleType = (Class<? extends IBaseBundle>) context.getResourceDefinition("Bundle").getImplementingClass();
			client.loadPage().byUrl(url).andReturnBundle(bundleType).execute();
		} catch (Exception e) {
			returnsResource = handleClientException(client, e, theModel);
		}
		long delay = System.currentTimeMillis() - start;

		String outcomeDescription = "Bundle Page";

		processAndAddLastClientInvocation(client, returnsResource, theModel, delay, outcomeDescription, interceptor, theRequest);

		return "result";
	}
}
