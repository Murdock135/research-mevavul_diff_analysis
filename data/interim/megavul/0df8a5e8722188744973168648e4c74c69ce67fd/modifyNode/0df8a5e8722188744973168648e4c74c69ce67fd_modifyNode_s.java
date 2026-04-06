class modifyNode {
@ApiOperation(value = "Modify Node Operation")
	@RequestMapping(value = "/modifyNode", method = RequestMethod.POST)
	@ResponseBody
	public String modifyNode(@RequestParam(value = "userid", required = true) String userId,
			@RequestParam(value = "solutionid", required = false) String solutionId,
			@RequestParam(value = "version", required = false) String version,
			@RequestParam(value = "cid", required = false) String cid,
			@RequestParam(value = "nodeid", required = true) String nodeId,
			@RequestParam(value = "nodename", required = false) String nodeName,
			@RequestParam(value = "ndata", required = false) String ndata,
			@RequestBody(required = false) DataConnector dataConnector) {
		
		String result = null;
		FieldMap fieldMap = null;
		DataBrokerMap databrokerMap = null;
		CollatorMap collatorMap = null;
		SplitterMap  splitterMap = null;
		logger.debug(EELFLoggerDelegator.debugLogger, "------- modifyNode() ------- : Begin");
		try {
			if(null != dataConnector){
				if(null != dataConnector.getFieldMap()){
					fieldMap = dataConnector.getFieldMap();
				}
				if(null != dataConnector.getDatabrokerMap()){
					databrokerMap = dataConnector.getDatabrokerMap();
				}
				if(null != dataConnector.getCollatorMap()){
					collatorMap = dataConnector.getCollatorMap();
				}
				if(null != dataConnector.getSplitterMap()){
					splitterMap = dataConnector.getSplitterMap();
				}
			}
			result = solutionService.modifyNode(userId, solutionId, version, cid, nodeId, nodeName, ndata, fieldMap, databrokerMap, collatorMap, splitterMap);
		} catch (Exception e) {
			logger.error(EELFLoggerDelegator.errorLogger, "-------Exception in  modifyNode() -------", e);
		}
		logger.debug(EELFLoggerDelegator.debugLogger, "------- modifyNode() ------- : End");
		return result;
	}
}
