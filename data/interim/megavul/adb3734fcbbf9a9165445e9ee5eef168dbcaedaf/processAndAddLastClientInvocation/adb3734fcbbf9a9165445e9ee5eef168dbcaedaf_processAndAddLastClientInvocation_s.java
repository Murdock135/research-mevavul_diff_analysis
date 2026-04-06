class processAndAddLastClientInvocation {
protected void processAndAddLastClientInvocation(GenericClient theClient, ResultType theResultType, ModelMap theModelMap, long theLatency, String outcomeDescription,
																	 CaptureInterceptor theInterceptor, HomeRequest theRequest) {
		try {
//			ApacheHttpRequest lastRequest = theInterceptor.getLastRequest();
//			HttpResponse lastResponse = theInterceptor.getLastResponse();
//			String requestBody = null;
//			String requestUrl = lastRequest != null ? lastRequest.getApacheRequest().getURI().toASCIIString() : null;
//			String action = lastRequest != null ? lastRequest.getApacheRequest().getMethod() : null;
//			String resultStatus = lastResponse != null ? lastResponse.getStatusLine().toString() : null;
//			String resultBody = StringUtils.defaultString(theInterceptor.getLastResponseBody());
//
//			if (lastRequest instanceof HttpEntityEnclosingRequest) {
//				HttpEntity entity = ((HttpEntityEnclosingRequest) lastRequest).getEntity();
//				if (entity.isRepeatable()) {
//					requestBody = IOUtils.toString(entity.getContent());
//				}
//			}
//
//			ContentType ct = lastResponse != null ? ContentType.get(lastResponse.getEntity()) : null;
//			String mimeType = ct != null ? ct.getMimeType() : null;


			IHttpRequest lastRequest = theInterceptor.getLastRequest();
			IHttpResponse lastResponse = theInterceptor.getLastResponse();
			String requestBody = null;
			String requestUrl = null;
			String action = null;
			String resultStatus = null;
			String resultBody = null;
			String mimeType = null;
			ContentType ct = null;
			if (lastRequest != null) {
				requestBody = lastRequest.getRequestBodyFromStream();
				requestUrl = lastRequest.getUri();
				action = lastRequest.getHttpVerbName();
			}
			if (lastResponse != null) {
				resultStatus = "HTTP " + lastResponse.getStatus() + " " + lastResponse.getStatusInfo();
				lastResponse.bufferEntity();
				try (InputStream input = lastResponse.readEntity()) {
					resultBody = IOUtils.toString(input, Constants.CHARSET_UTF8);
				}

				List<String> ctStrings = lastResponse.getHeaders(Constants.HEADER_CONTENT_TYPE);
				if (ctStrings != null && ctStrings.isEmpty() == false) {
					ct = ContentType.parse(ctStrings.get(0));
					mimeType = ct.getMimeType();
				}
			}

			EncodingEnum ctEnum = EncodingEnum.forContentType(mimeType);
			String narrativeString = "";

			StringBuilder resultDescription = new StringBuilder();
			IBaseResource riBundle = null;

			FhirContext context = getContext(theRequest);
			if (ctEnum == null) {
				resultDescription.append("Non-FHIR response");
			} else {
				switch (ctEnum) {
					case JSON:
						if (theResultType == ResultType.RESOURCE) {
							narrativeString = parseNarrative(theRequest, ctEnum, resultBody);
							resultDescription.append("JSON resource");
						} else if (theResultType == ResultType.BUNDLE) {
							resultDescription.append("JSON bundle");
							riBundle = context.newJsonParser().parseResource(resultBody);
						}
						break;
					case XML:
					default:
						if (theResultType == ResultType.RESOURCE) {
							narrativeString = parseNarrative(theRequest, ctEnum, resultBody);
							resultDescription.append("XML resource");
						} else if (theResultType == ResultType.BUNDLE) {
							resultDescription.append("XML bundle");
							riBundle = context.newXmlParser().parseResource(resultBody);
						}
						break;
				}
			}

			resultDescription.append(" (").append(defaultString(resultBody).length() + " bytes)");

			Header[] requestHeaders = lastRequest != null ? applyHeaderFilters(lastRequest.getAllHeaders()) : new Header[0];
			Header[] responseHeaders = lastResponse != null ? applyHeaderFilters(lastResponse.getAllHeaders()) : new Header[0];

			theModelMap.put("outcomeDescription", outcomeDescription);
			theModelMap.put("resultDescription", resultDescription.toString());
			theModelMap.put("action", action);
			theModelMap.put("ri", riBundle instanceof IAnyResource);
			theModelMap.put("riBundle", riBundle);
			theModelMap.put("resultStatus", resultStatus);

			theModelMap.put("requestUrl", requestUrl);
			theModelMap.put("requestUrlText", formatUrl(theClient.getUrlBase(), requestUrl));

			String requestBodyText = format(requestBody, ctEnum);
			theModelMap.put("requestBody", requestBodyText);

			String resultBodyText = format(resultBody, ctEnum);
			theModelMap.put("resultBody", resultBodyText);

			theModelMap.put("resultBodyIsLong", resultBodyText.length() > 1000);
			theModelMap.put("requestHeaders", requestHeaders);
			theModelMap.put("responseHeaders", responseHeaders);
			theModelMap.put("narrative", narrativeString);
			theModelMap.put("latencyMs", theLatency);

		} catch (Exception e) {
			ourLog.error("Failure during processing", e);
			theModelMap.put("errorMsg", toDisplayError("Error during processing: " + e.getMessage(), e));
		}

	}
}
