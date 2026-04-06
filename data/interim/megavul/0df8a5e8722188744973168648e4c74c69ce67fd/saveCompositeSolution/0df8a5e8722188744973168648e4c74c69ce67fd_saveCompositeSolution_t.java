class saveCompositeSolution {
@ApiOperation(value = "Save the Composite Solution")
	@RequestMapping(value = "/saveCompositeSolution", method = RequestMethod.POST)
	@ResponseBody
	public Object saveCompositeSolution(HttpServletRequest request,
			@RequestParam(value = "userId", required = true) String userId,
			@RequestParam(value = "solutionName", required = true) String solutionName,
			@RequestParam(value = "version", required = true) String version,
			@RequestParam(value = "solutionId", required = false) String solutionId,
			@RequestParam(value = "description", required = true) String description,
			@RequestParam(value = "cid", required = false) String cid,
			@RequestParam(value = "ignoreLesserVersionConflictFlag", required = true, defaultValue = "false") boolean ignoreLesserVersionConflictFlag)
			throws AcumosException {

		String result = "";
		String error = "{\"errorCode\" : \"%s\", \"errorDescription\" : \"%s\"}";
		logger.debug(EELFLoggerDelegator.debugLogger, " saveCompositeSolution() Begin ");

		DSCompositeSolution dscs = new DSCompositeSolution();

		try {
			dscs.setAuthor(userId);
			dscs.setSolutionName(solutionName);
			dscs.setSolutionId(SanitizeUtils.sanitize(solutionId));
			dscs.setVersion(version);
			dscs.setOnBoarder(userId);
			dscs.setDescription(description);
			dscs.setProvider(props.getProvider());
			dscs.setToolKit(props.getToolKit());
			dscs.setVisibilityLevel(props.getVisibilityLevel());
			dscs.setcId(cid);
			dscs.setIgnoreLesserVersionConflictFlag(ignoreLesserVersionConflictFlag);

			// 1. JSON Validation
			if (DSUtil.isValidJSON(dscs.toJsonString())) {
				logger.debug(EELFLoggerDelegator.debugLogger, " SuccessFully validated inputJson ");
				// 2. Mandatory Value validation
				String isValidmsg = checkMandatoryFieldsforSave(dscs);

				if (null != isValidmsg) {
					result = String.format(error, "603", isValidmsg);
				} else {
					logger.debug(EELFLoggerDelegator.debugLogger,
							" SuccessFully validated mandatory fields ");
					result = compositeServiceImpl.saveCompositeSolution(dscs);
				}
			} else {
				result = String.format(error, "200", "Incorrectly formatted input – Invalid JSON");
			}

		} catch (AcumosException e) {
			logger.error(EELFLoggerDelegator.errorLogger, " Exception in getSolutions() ", e);
			result = String.format(error, e.getErrorCode(), e.getErrorDesc());
		} catch (Exception e) {
			logger.error(EELFLoggerDelegator.errorLogger, " Exception in getSolutions() ", e);
			result = String.format(error, props.getCompositionSolutionErrorCode(),
					props.getCompositionSolutionErrorDesc());
		}
		logger.debug(EELFLoggerDelegator.debugLogger, " saveCompositeSolution() End ");
		return result;
	}
}
