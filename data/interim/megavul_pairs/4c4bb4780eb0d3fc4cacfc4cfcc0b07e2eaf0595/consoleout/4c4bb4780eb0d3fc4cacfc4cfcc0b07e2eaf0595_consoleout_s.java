class consoleout {
@RequestMapping(value = "/**/consoleout.json", method = RequestMethod.GET)
    public ModelAndView consoleout(@RequestParam("pipelineName") String pipelineName,
                                   @RequestParam("pipelineCounter") String pipelineCounter,
                                   @RequestParam("stageName") String stageName,
                                   @RequestParam("buildName") String buildName,
                                   @RequestParam(value = "stageCounter", required = false) String stageCounter,
                                   @RequestParam(value = "startLineNumber", required = false) Long start
    ) {
        start = start == null ? 0L : start;

        try {
            JobIdentifier identifier = restfulService.findJob(pipelineName, pipelineCounter, stageName, stageCounter, buildName);
            if (jobInstanceDao.isJobCompleted(identifier) && !consoleService.doesLogExist(identifier)) {
                return logsNotFound(identifier);
            }
            ConsoleConsumer streamer = consoleService.getStreamer(start, identifier);
            return new ModelAndView(new ConsoleOutView(streamer, consoleLogCharset));
        } catch (Exception e) {
            return buildNotFound(pipelineName, pipelineCounter, stageName, stageCounter, buildName);
        }
    }
}
