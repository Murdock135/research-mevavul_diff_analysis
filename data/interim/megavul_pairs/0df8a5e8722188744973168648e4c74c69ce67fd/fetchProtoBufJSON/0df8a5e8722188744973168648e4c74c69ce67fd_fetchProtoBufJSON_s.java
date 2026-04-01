class fetchProtoBufJSON {
@ApiOperation(value = "Get the profobuf file details for specified solutionID and version")
	@RequestMapping(value = "/fetchProtoBufJSON", method = RequestMethod.GET, produces = "text/plain")
	@ResponseBody
	public String fetchProtoBufJSON(@RequestParam(value = "userId", required = true) String userId,
			@RequestParam(value = "solutionId", required = true) String solutionId,
			@RequestParam(value = "version", required = true) String version) {
		logger.debug(EELFLoggerDelegator.debugLogger,
				" fetchProtoBufJSON() : Begin");

		String resultTemplate = "{\"protobuf_json\" : %s,\n \"success\" : \"%s\",\n \"errorMessage\" : \"%s\"}";
		String result = "";
		try {
			result = iacumosCatalog.readArtifact(userId, solutionId, version, props.getProtoArtifactType().trim());

			if (result != null && !result.isEmpty()) {
				resultTemplate = String.format(resultTemplate, result, true, "");
			} else {
				resultTemplate = String.format(resultTemplate, result, false, "Unable to read protoBufFile");
			}
		} catch (Exception e) {
			logger.error(EELFLoggerDelegator.errorLogger, "Exception in fetchProtoBufJSON() ", e);
			resultTemplate = String.format(resultTemplate, null, false, e.getMessage());
		}
		logger.debug(EELFLoggerDelegator.debugLogger,
				"fetchProtoBufJSON() : End");
		return resultTemplate;
	}
}
