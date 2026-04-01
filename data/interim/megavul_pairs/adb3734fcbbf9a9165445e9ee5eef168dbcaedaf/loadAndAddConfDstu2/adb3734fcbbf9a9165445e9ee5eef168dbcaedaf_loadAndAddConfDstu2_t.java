class loadAndAddConfDstu2 {
private IResource loadAndAddConfDstu2(HttpServletRequest theServletRequest, final HomeRequest theRequest, final ModelMap theModel) {
		CaptureInterceptor interceptor = new CaptureInterceptor();
		GenericClient client = theRequest.newClient(theServletRequest, getContext(theRequest), myConfig, interceptor);

		ca.uhn.fhir.model.dstu2.resource.Conformance conformance;
		try {
			conformance = client.fetchConformance().ofType(Conformance.class).execute();
		} catch (Exception e) {
			ourLog.warn("Failed to load conformance statement, error was: {}", e.toString());
			theModel.put("errorMsg", toDisplayError("Failed to load conformance statement, error was: " + e.toString(), e));
			conformance = new ca.uhn.fhir.model.dstu2.resource.Conformance();
		}

		theModel.put("jsonEncodedConf", getContext(theRequest).newJsonParser().encodeResourceToString(conformance));

		Map<String, Number> resourceCounts = new HashMap<>();
		long total = 0;
		for (ca.uhn.fhir.model.dstu2.resource.Conformance.Rest nextRest : conformance.getRest()) {
			for (ca.uhn.fhir.model.dstu2.resource.Conformance.RestResource nextResource : nextRest.getResource()) {
				List<ExtensionDt> exts = nextResource.getUndeclaredExtensionsByUrl(RESOURCE_COUNT_EXT_URL);
				if (exts != null && exts.size() > 0) {
					Number nextCount = ((DecimalDt) (exts.get(0).getValue())).getValueAsNumber();
					resourceCounts.put(nextResource.getTypeElement().getValue(), nextCount);
					total += nextCount.longValue();
				}
			}
		}
		theModel.put("resourceCounts", resourceCounts);

		if (total > 0) {
			for (ca.uhn.fhir.model.dstu2.resource.Conformance.Rest nextRest : conformance.getRest()) {
				Collections.sort(nextRest.getResource(), new Comparator<ca.uhn.fhir.model.dstu2.resource.Conformance.RestResource>() {
					@Override
					public int compare(ca.uhn.fhir.model.dstu2.resource.Conformance.RestResource theO1, ca.uhn.fhir.model.dstu2.resource.Conformance.RestResource theO2) {
						DecimalDt count1 = new DecimalDt();
						List<ExtensionDt> count1exts = theO1.getUndeclaredExtensionsByUrl(RESOURCE_COUNT_EXT_URL);
						if (count1exts != null && count1exts.size() > 0) {
							count1 = (DecimalDt) count1exts.get(0).getValue();
						}
						DecimalDt count2 = new DecimalDt();
						List<ExtensionDt> count2exts = theO2.getUndeclaredExtensionsByUrl(RESOURCE_COUNT_EXT_URL);
						if (count2exts != null && count2exts.size() > 0) {
							count2 = (DecimalDt) count2exts.get(0).getValue();
						}
						int retVal = count2.compareTo(count1);
						if (retVal == 0) {
							retVal = theO1.getTypeElement().getValue().compareTo(theO2.getTypeElement().getValue());
						}
						return retVal;
					}
				});
			}
		}

		theModel.put("conf", conformance);
		theModel.put("requiredParamExtension", ExtensionConstants.PARAM_IS_REQUIRED);

		return conformance;
	}
}
