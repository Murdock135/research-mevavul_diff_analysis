class postArtifact {
@RequestMapping(value = "/repository/restful/artifact/POST/*", method = RequestMethod.POST)
    public ModelAndView postArtifact(@RequestParam("pipelineName") String pipelineName,
                                     @RequestParam("pipelineCounter") String pipelineCounter,
                                     @RequestParam("stageName") String stageName,
                                     @RequestParam(value = "stageCounter", required = false) String stageCounter,
                                     @RequestParam("buildName") String buildName,
                                     @RequestParam(value = "buildId", required = false) Long buildId,
                                     @RequestParam("filePath") String filePath,
                                     @RequestParam(value = "attempt", required = false) Integer attempt,
                                     MultipartHttpServletRequest request) throws Exception {
        JobIdentifier jobIdentifier;
        if (!headerConstraint.isSatisfied(request)) {
            return ResponseCodeView.create(HttpServletResponse.SC_BAD_REQUEST, "Missing required header 'Confirm'");
        }
        if (!isValidStageCounter(stageCounter)) {
            return buildNotFound(pipelineName, pipelineCounter, stageName, stageCounter, buildName);
        }
        try {
            jobIdentifier = restfulService.findJob(pipelineName, pipelineCounter, stageName, stageCounter,
                    buildName, buildId);
        } catch (Exception e) {
            return buildNotFound(pipelineName, pipelineCounter, stageName, stageCounter,
                    buildName);
        }

        int convertedAttempt = attempt == null ? 1 : attempt;

        try {
            File artifact = artifactsService.findArtifact(jobIdentifier, filePath);
            if (artifact.exists() && artifact.isFile()) {
                return FileModelAndView.fileAlreadyExists(filePath);
            }

            MultipartFile multipartFile = multipartFile(request);
            if (multipartFile == null) {
                return FileModelAndView.invalidUploadRequest();
            }

            boolean success = saveFile(convertedAttempt, artifact, multipartFile, shouldUnzipStream(multipartFile));

            if (!success) {
                return FileModelAndView.errorSavingFile(filePath);
            }

            success = updateChecksumFile(request, jobIdentifier, filePath);

            if (!success) {
                return FileModelAndView.errorSavingChecksumFile(filePath);
            }

            return FileModelAndView.fileCreated(filePath);

        } catch (IllegalArtifactLocationException e) {
            return FileModelAndView.forbiddenUrl(filePath);
        }
    }
}
