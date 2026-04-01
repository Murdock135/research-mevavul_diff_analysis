class closeCompositeSolution {
@ApiOperation(value = "Close Composite Solution Operation")
	@RequestMapping(value = "/closeCompositeSolution ", method = RequestMethod.POST)
	@ResponseBody
	public String closeCompositeSolution(@RequestParam(value = "userId", required = true) String userId,
			@RequestParam(value = "solutionId", required = false) String solutionId,
			@RequestParam(value = "solutionVersion", required = false) String solutionVersion,
			@RequestParam(value = "cid", required = false) String cid) {
		logger.debug(EELFLoggerDelegator.debugLogger, " closeCompositeSolution(): Begin ");
		String result = "";
		try {
			result = compositeServiceImpl.closeCompositeSolution(userId, SanitizeUtils.sanitize(solutionId), solutionVersion, cid);
		} catch (Exception e) {
			logger.error(EELFLoggerDelegator.errorLogger, " Exception in closeCompositeSolution() ", e);
		}
		logger.debug(EELFLoggerDelegator.debugLogger, " closeCompositeSolution(): End ");
		return result;
	}
}
