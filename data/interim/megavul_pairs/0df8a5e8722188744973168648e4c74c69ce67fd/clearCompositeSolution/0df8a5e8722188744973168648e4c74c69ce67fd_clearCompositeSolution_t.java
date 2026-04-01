class clearCompositeSolution {
@ApiOperation(value = "Clear Composite Solution Operation")
	@RequestMapping(value = "/clearCompositeSolution", method = RequestMethod.POST)
	@ResponseBody
	public String clearCompositeSolution(@RequestParam(value = "userId", required = true) String userId,
			@RequestParam(value = "solutionId", required = false) String solutionId,
			@RequestParam(value = "solutionVersion", required = false) String solutionVersion,
			@RequestParam(value = "cid", required = false) String cid) {
		logger.debug(EELFLoggerDelegator.debugLogger, " clearCompositeSolution(): Begin ");
		String result = "";
		try {
			result = compositeServiceImpl.clearCompositeSolution(userId, SanitizeUtils.sanitize(solutionId), solutionVersion, cid);
		} catch (Exception e) {
			logger.error(EELFLoggerDelegator.errorLogger, " Exception in clearCompositeSolution() ", e);
		}
		logger.debug(EELFLoggerDelegator.debugLogger, " clearCompositeSolution(): End ");
		return result;

	}
}
