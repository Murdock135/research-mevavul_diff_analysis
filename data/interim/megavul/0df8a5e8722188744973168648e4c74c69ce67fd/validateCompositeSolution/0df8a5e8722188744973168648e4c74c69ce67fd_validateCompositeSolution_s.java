class validateCompositeSolution {
@ApiOperation(value = "Validate Composite Solution")
	@RequestMapping(value = "/validateCompositeSolution", method = RequestMethod.POST, produces = "text/plain")
	@ResponseBody
	public String validateCompositeSolution(@RequestParam(value = "userId", required = true) String userId,
			@RequestParam(value = "solutionName", required = true) String solutionName,
			@RequestParam(value = "solutionId", required = true) String solutionId,
			@RequestParam(value = "version", required = true) String version) {
		logger.debug(EELFLoggerDelegator.debugLogger, "validateCompositeSolution() : Begin ");
		String result = "";
		try {
			result = compositeServiceImpl.validateCompositeSolution(userId, solutionName, solutionId, version);
			result = String.format(result);
		} catch (Exception e) {
			result = "{\"success\" : \"false\", \"errorDescription\" : \"Failed to Validate Composite Solution\"}";
			result = String.format(result);
			logger.debug(EELFLoggerDelegator.errorLogger, " Exception in validateCompositeSolution() ", e);
			e.printStackTrace();
		}
		logger.debug(EELFLoggerDelegator.debugLogger, "validateCompositeSolution() : End ");
		return result;
	}
}
