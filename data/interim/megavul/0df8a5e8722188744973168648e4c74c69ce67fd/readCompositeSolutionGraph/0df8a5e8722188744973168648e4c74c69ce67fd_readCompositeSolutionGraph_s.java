class readCompositeSolutionGraph {
@ApiOperation(value = "Gets existing composite solution details for specified solutionId and version")
	@RequestMapping(value = "/readCompositeSolutionGraph", method = RequestMethod.GET, produces = "text/plain")
	@ResponseBody
	public String readCompositeSolutionGraph(@RequestParam(value = "userId", required = true) String userId,
			@RequestParam(value = "solutionId", required = true) String solutionId,
			@RequestParam(value = "version", required = true) String version) {
		logger.debug(EELFLoggerDelegator.debugLogger, " fetchJsonTOSCA()  : Begin");
		String result;
		try {
			result = solutionService.readCompositeSolutionGraph(userId, solutionId, version);
		} catch (Exception e) {
			logger.error(EELFLoggerDelegator.errorLogger, "Failed to read the ComposietSolution", e);
			result = "";
		}
		logger.debug(EELFLoggerDelegator.debugLogger, " fetchJsonTOSCA()  : End");
		return result;
	}
}
