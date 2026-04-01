class doActionHistory {
private void doActionHistory(HttpServletRequest theReq, HomeRequest theRequest, BindingResult theBindingResult, ModelMap theModel, String theMethod, String theMethodDescription) {
		addCommonParams(theReq, theRequest, theModel);

		CaptureInterceptor interceptor = new CaptureInterceptor();
		GenericClient client = theRequest.newClient(theReq, getContext(theRequest), myConfig, interceptor);

		String id = null;
		Class<? extends IBaseResource> type = null; // def.getImplementingClass();
		if ("history-type".equals(theMethod)) {
			RuntimeResourceDefinition def = getContext(theRequest).getResourceDefinition(theRequest.getResource());
			type = def.getImplementingClass();
			id = sanitizeUrlPart(defaultString(theReq.getParameter("resource-history-id")));
		}

		DateTimeDt since = null;
		String sinceStr = sanitizeUrlPart(theReq.getParameter("since"));
		if (isNotBlank(sinceStr)) {
			since = new DateTimeDt(sinceStr);
		}

		Integer limit = null;
		String limitStr = sanitizeUrlPart(theReq.getParameter("limit"));
		if (isNotBlank(limitStr)) {
			limit = Integer.parseInt(limitStr);
		}

		ResultType returnsResource = ResultType.BUNDLE;

		long start = System.currentTimeMillis();
		try {
			ourLog.info(logPrefix(theModel) + "Retrieving history for type {} ID {} since {}", new Object[] { type, id, since });

			IHistory hist0 = client.history();
			IHistoryUntyped hist1;
			if (isNotBlank(id)) {
				hist1 = hist0.onInstance(new IdDt(theRequest.getResource(), id));
			} else if (type != null) {
				hist1 = hist0.onType(type);
			} else {
				hist1 = hist0.onServer();
			}

			IHistoryTyped<?> hist2;
			hist2 = hist1.andReturnBundle(client.getFhirContext().getResourceDefinition("Bundle").getImplementingClass(IBaseBundle.class));

			if (since != null) {
				hist2.since(since);
			}
			if (limit != null) {
				hist2.count(limit);
			}

			hist2.execute();
		} catch (Exception e) {
			returnsResource = handleClientException(client, e, theModel);
		}
		long delay = System.currentTimeMillis() - start;

		processAndAddLastClientInvocation(client, returnsResource, theModel, delay, theMethodDescription, interceptor, theRequest);

	}
}
