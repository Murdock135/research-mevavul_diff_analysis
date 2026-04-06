class addNode {
@ApiOperation(value = "add Node Operation")
	@RequestMapping(value = "/addNode", method = RequestMethod.POST)
	public String addNode(@RequestParam(value = "userId", required = true) String userId,
			@RequestParam(value = "solutionId", required = false) String solutionId,
			@RequestParam(value = "version", required = false) String version,
			@RequestParam(value = "cid", required = false) String cid, @RequestBody @Valid Nodes node) {
		String results = "";
		logger.debug(EELFLoggerDelegator.debugLogger, " addNode()  : Begin");
		try {

			boolean validNode = validateNode(node);
			if (validNode) {
				if ((solutionId != null && version != null) || (null != cid)) {
					results = solutionService.addNode(userId, solutionId, version, cid, node);
				} else {
					results = "{\"error\": \"Either Cid or SolutionId and Version need to Pass\"}";
				}
			} else {
				results = "{\"error\": \"JSON schema not valid, Please check the input JSON\"}";
			}
		} catch (Exception e) {
			logger.error(EELFLoggerDelegator.errorLogger, "Exception in  addNode() ", e);
		}
		logger.debug(EELFLoggerDelegator.debugLogger, " addNode()  : End");
		return results;

	}
}
