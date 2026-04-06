class deleteNode {
@ApiOperation(value = "delete Node Operation")
	@RequestMapping(value = "/deleteNode", method = RequestMethod.POST)
	public String deleteNode(@RequestParam(value = "userId", required = true) String userId,
			@RequestParam(value = "solutionId", required = false) String solutionId,
			@RequestParam(value = "version", required = false) String version,
			@RequestParam(value = "cid", required = false) String cid,
			@RequestParam(value = "nodeId", required = true) String nodeId) {
		logger.debug(EELFLoggerDelegator.debugLogger, " deleteNode() in SolutionController Begin ");
		String result = "";
		String resultTemplate = "{\"success\":\"%s\", \"errorMessage\":\"%s\"}";
		if (null == userId && null == nodeId) {
			result = String.format(resultTemplate, false, "Mandatory feild(s) missing");
		} else {
			try {
				boolean deletedNode = solutionService.deleteNode(userId, SanitizeUtils.sanitize(solutionId), version, cid, nodeId);
				if (deletedNode) {
					result = String.format(resultTemplate, true, "");
				} else {
					result = String.format(resultTemplate, false, "Invalid Node Id – not found");
				}
			} catch (Exception e) {
				logger.error(EELFLoggerDelegator.errorLogger,
						" Exception in deleteNode() in SolutionController ", e);
			}
		}
		logger.debug(EELFLoggerDelegator.debugLogger, " deleteNode() in SolutionController Ends ");
		return result;
	}
}
