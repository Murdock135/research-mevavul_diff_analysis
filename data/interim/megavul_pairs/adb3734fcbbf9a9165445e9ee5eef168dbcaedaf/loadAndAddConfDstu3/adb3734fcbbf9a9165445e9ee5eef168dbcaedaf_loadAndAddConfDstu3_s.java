class loadAndAddConfDstu3 {
private IBaseResource loadAndAddConfDstu3(HttpServletRequest theServletRequest, final HomeRequest theRequest, final ModelMap theModel) {
		CaptureInterceptor interceptor = new CaptureInterceptor();
		GenericClient client = theRequest.newClient(theServletRequest, getContext(theRequest), myConfig, interceptor);

		org.hl7.fhir.dstu3.model.CapabilityStatement capabilityStatement = new CapabilityStatement();
		try {
			capabilityStatement = client.fetchConformance().ofType(org.hl7.fhir.dstu3.model.CapabilityStatement.class).execute();
		} catch (Exception ex) {
			ourLog.warn("Failed to load conformance statement, error was: {}", ex.toString());
			theModel.put("errorMsg", toDisplayError("Failed to load conformance statement, error was: " + ex.toString(), ex));
		}

		theModel.put("jsonEncodedConf", getContext(theRequest).newJsonParser().encodeResourceToString(capabilityStatement));

		Map<String, Number> resourceCounts = new HashMap<String, Number>();
		long total = 0;

		for (CapabilityStatementRestComponent nextRest : capabilityStatement.getRest()) {
			for (CapabilityStatementRestResourceComponent nextResource : nextRest.getResource()) {
				List<Extension> exts = nextResource.getExtensionsByUrl(RESOURCE_COUNT_EXT_URL);
				if (exts != null && exts.size() > 0) {
					Number nextCount = ((DecimalType) (exts.get(0).getValue())).getValueAsNumber();
					resourceCounts.put(nextResource.getTypeElement().getValue(), nextCount);
					total += nextCount.longValue();
				}
			}
		}

		theModel.put("resourceCounts", resourceCounts);

		if (total > 0) {
			for (CapabilityStatementRestComponent nextRest : capabilityStatement.getRest()) {
				Collections.sort(nextRest.getResource(), new Comparator<CapabilityStatementRestResourceComponent>() {
					@Override
					public int compare(CapabilityStatementRestResourceComponent theO1, CapabilityStatementRestResourceComponent theO2) {
						DecimalType count1 = new DecimalType();
						List<Extension> count1exts = theO1.getExtensionsByUrl(RESOURCE_COUNT_EXT_URL);
						if (count1exts != null && count1exts.size() > 0) {
							count1 = (DecimalType) count1exts.get(0).getValue();
						}
						DecimalType count2 = new DecimalType();
						List<Extension> count2exts = theO2.getExtensionsByUrl(RESOURCE_COUNT_EXT_URL);
						if (count2exts != null && count2exts.size() > 0) {
							count2 = (DecimalType) count2exts.get(0).getValue();
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

		theModel.put("requiredParamExtension", ExtensionConstants.PARAM_IS_REQUIRED);

		theModel.put("conf", capabilityStatement);
		return capabilityStatement;
	}
}
