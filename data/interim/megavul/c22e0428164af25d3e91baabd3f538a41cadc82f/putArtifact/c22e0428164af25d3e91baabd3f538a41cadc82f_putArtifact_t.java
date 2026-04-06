class putArtifact {
@RequestMapping(value = "/repository/restful/artifact/PUT/*", method = RequestMethod.PUT)
    public ModelAndView putArtifact(@RequestParam("pipelineName") String pipelineName,
                                    @RequestParam("pipelineCounter") String pipelineCounter,
                                    @RequestParam("stageName") String stageName,
                                    @RequestParam(value = "stageCounter", required = false) String stageCounter,
                                    @RequestParam("buildName") String buildName,
                                    @RequestParam(value = "buildId", required = false) Long buildId,
                                    @RequestParam("filePath") String filePath,
                                    @RequestParam(value = "agentId", required = false) String agentId,
                                    HttpServletRequest request
    ) throws Exception {
        if (filePath.contains("..")) {
            return FileModelAndView.forbiddenUrl(filePath);
        }

        if (!isValidStageCounter(stageCounter)) {
            return buildNotFound(pipelineName, pipelineCounter, stageName, stageCounter, buildName);
        }

        JobIdentifier jobIdentifier;
        try {
            jobIdentifier = restfulService.findJob(pipelineName, pipelineCounter, stageName, stageCounter, buildName, buildId);
        } catch (Exception e) {
            return buildNotFound(pipelineName, pipelineCounter, stageName, stageCounter, buildName);
        }

        if (isConsoleOutput(filePath)) {
            return putConsoleOutput(jobIdentifier, request.getInputStream());
        } else {
            return putArtifact(jobIdentifier, filePath, request.getInputStream());
        }
    }
}
