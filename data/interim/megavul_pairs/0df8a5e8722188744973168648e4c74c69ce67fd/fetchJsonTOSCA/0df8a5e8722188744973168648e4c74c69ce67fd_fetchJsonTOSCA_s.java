class fetchJsonTOSCA {
@ApiOperation(value = "Gets TOSCA details for specified solutionId and version")
	@RequestMapping(value = "/fetchJsonTOSCA", method = RequestMethod.GET, produces = "text/plain")
	@ResponseBody
	public String fetchJsonTOSCA(@RequestParam(value = "userId", required = true) String userId,
			@RequestParam(value = "solutionId", required = true) String solutionId,
			@RequestParam(value = "version", required = true) String version, HttpServletResponse response) {
		logger.debug(EELFLoggerDelegator.debugLogger, "fetchJsonTOSCA() : Begin");
		String result = "";
		try {
			result = iacumosCatalog.readArtifact(userId, solutionId, version, props.getArtifactType().trim());
			
			if (result == null || result.isEmpty()) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				result = "Failed to fetch the TOSCA details for specified solutionId and version";				
			}
		} catch (Exception e) {
			logger.error(EELFLoggerDelegator.errorLogger, "Exception in fetchJsonTOSCA() ", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			result = e.getMessage();
		}
		logger.debug(EELFLoggerDelegator.debugLogger, "fetchJsonTOSCA() : End");
		return result;
	}
}
