class deleteCompositeSolution {
@ApiOperation(value = "Delete the CompositeSolution")
	@RequestMapping(value = "/deleteCompositeSolution", method = RequestMethod.POST)
	@ResponseBody
	public String deleteCompositeSolution(@RequestParam(value = "userid", required = true) String userId,
			@RequestParam(value = "solutionid", required = true) String solutionId,
			@RequestParam(value = "version", required = true) String version) {
		String resultTemplate = "{\"success\":\"%s\",\"errorMessage\":\"%s\"}";
		String result = "";
		logger.debug(EELFLoggerDelegator.debugLogger, " deleteCompositeSolution()  : Begin");

		try {

			boolean deleted = compositeServiceImpl.deleteCompositeSolution(userId, solutionId, version);
			if (!deleted) {
				result = String.format(resultTemplate, "false", "Requested Solution Not Found");
			} else {
				result = String.format(resultTemplate, "true", "");
			}
		} catch (Exception e) {
			logger.debug(EELFLoggerDelegator.debugLogger, "Exception in  deleteCompositeSolution() ", e);
			result = String.format(resultTemplate, "false", "Exception : Requested Solution Not Found");
		}
		logger.debug(EELFLoggerDelegator.debugLogger, " deleteCompositeSolution()  : End");
		return result;
	}
}
