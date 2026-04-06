class deleteLink {
@ApiOperation(value = "delete link Operation")
	@RequestMapping(value = "/deleteLink", method = RequestMethod.POST)
	public String deleteLink(@RequestParam(value = "userId", required = true) String userId,
			@RequestParam(value = "cid", required = false) String cid,
			@RequestParam(value = "solutionId", required = false) String solutionId,
			@RequestParam(value = "version", required = false) String version,
			@RequestParam(value = "linkId", required = true) String linkId) {

		logger.debug(EELFLoggerDelegator.debugLogger, " deleteLink() in SolutionController begins -");
		String result = "";
		String resultTemplate = "{\"success\":\"%s\", \"errorMessage\":\"%s\"}";
		if (null == userId && null == linkId) {
			result = String.format(resultTemplate, false, "Mandatory feild(s) missing");
		} else {
			try {
				boolean deletedLink = solutionService.deleteLink(userId, SanitizeUtils.sanitize(solutionId), version, cid, linkId);
				if (deletedLink) {
					result = String.format(resultTemplate, true, "");
				} else {
					result = String.format(resultTemplate, false, "Invalid Link Id – not found");
				}
			} catch (Exception e) {
				logger.error(EELFLoggerDelegator.errorLogger,
						" Exception in deleteLink() in SolutionController ", e);
			}
		}
		logger.debug(EELFLoggerDelegator.debugLogger, " deleteLink() in SolutionController Ends ");
		return result;
	}
}
