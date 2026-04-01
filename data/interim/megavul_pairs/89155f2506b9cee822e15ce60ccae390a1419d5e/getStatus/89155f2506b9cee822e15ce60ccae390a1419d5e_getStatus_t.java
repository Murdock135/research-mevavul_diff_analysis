class getStatus {
@RequestMapping(value = STATUS_URL + "/{referenceId:\\S+}.json", method = RequestMethod.GET)
    public final void getStatus(
            @PathVariable final String referenceId,
            final HttpServletRequest statusRequest,
            final HttpServletResponse statusResponse) {
        MDC.put(Processor.MDC_JOB_ID_KEY, referenceId);
        setNoCache(statusResponse);
        try {
            PrintJobStatus status = this.jobManager.getStatus(referenceId);

            setContentType(statusResponse);
            try (PrintWriter writer = statusResponse.getWriter()) {
                JSONWriter json = new JSONWriter(writer);
                json.object();
                {
                    json.key(JSON_DONE).value(status.isDone());
                    json.key(JSON_STATUS).value(status.getStatus().toString().toLowerCase());
                    json.key(JSON_ELAPSED_TIME).value(status.getElapsedTime());
                    json.key(JSON_WAITING_TIME).value(status.getWaitingTime());
                    if (!StringUtils.isEmpty(status.getError())) {
                        json.key(JSON_ERROR).value(status.getError());
                    }

                    addDownloadLinkToJson(statusRequest, referenceId, json);
                }
                json.endObject();
            }
        } catch (JSONException | IOException e) {
            throw ExceptionUtils.getRuntimeException(e);
        } catch (NoSuchReferenceException e) {
            error(statusResponse, e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
