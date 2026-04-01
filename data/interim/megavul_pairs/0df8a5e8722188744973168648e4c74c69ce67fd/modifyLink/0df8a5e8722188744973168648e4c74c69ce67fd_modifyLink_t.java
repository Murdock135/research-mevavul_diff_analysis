class modifyLink {
@ApiOperation(value = "Modify Link Operation")
	@RequestMapping(value = "/modifyLink", method = RequestMethod.POST)
	@ResponseBody
	public String modifyLink(@RequestParam(value = "userid", required = true) String userId,
			@RequestParam(value = "cid", required = false) String cid,
			@RequestParam(value = "solutionid", required = false) String solutionId,
			@RequestParam(value = "version", required = false) String version,
			@RequestParam(value = "linkid", required = true) String linkId,
			@RequestParam(value = "linkname", required = true) String linkName) {
		String result = null;
		logger.debug(EELFLoggerDelegator.debugLogger, " modifyLink()  : Begin");
		try {
			result = solutionService.modifyLink(userId, cid, SanitizeUtils.sanitize(solutionId), version, linkId, linkName);
		} catch (Exception e) {
			logger.error(EELFLoggerDelegator.errorLogger, "Exception in  modifyLink() ", e);
		}
		logger.debug(EELFLoggerDelegator.debugLogger, " modifyLink()  : End");
		return result;
	}
}
