class setProbeIndicator {
@ApiOperation(value = "set the ProbeIndicator")
	@RequestMapping(value = "/setProbeIndicator", method = RequestMethod.POST)
	public @ResponseBody SuccessErrorMessage setProbeIndicator(HttpServletRequest request,
			@RequestParam(value = "userId", required = true) String userId,
			@RequestParam(value = "solutionId", required = false) String solutionId,
			@RequestParam(value = "version", required = true) String version,
			@RequestParam(value = "cid", required = false) String cid,
			@RequestParam(value = "probeIndicator", required = true) String probeIndicator
			)
			throws AcumosException {
        SuccessErrorMessage successErrorMessage = null;
		logger.debug(EELFLoggerDelegator.debugLogger, "setProbeIndicator() in SolutionController Begin");
        try {
        	successErrorMessage = compositeServiceImpl.setProbeIndicator(userId, solutionId, version, cid,probeIndicator);
		}catch (Exception e) {
			logger.error(EELFLoggerDelegator.errorLogger, "Exception in setProbeIndicator() in SolutionController", e);
		}
		logger.debug(EELFLoggerDelegator.debugLogger, "setProbeIndicator() in SolutionController End");
		return successErrorMessage;
	}
}
