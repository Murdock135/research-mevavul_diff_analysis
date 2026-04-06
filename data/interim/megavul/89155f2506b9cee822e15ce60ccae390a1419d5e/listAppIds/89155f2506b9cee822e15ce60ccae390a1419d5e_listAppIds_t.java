class listAppIds {
@RequestMapping(value = LIST_APPS_URL, method = RequestMethod.GET)
    public final void listAppIds(
            final HttpServletResponse listAppsResponse) throws ServletException,
            IOException {
        MDC.remove(Processor.MDC_JOB_ID_KEY);
        setCache(listAppsResponse);
        Set<String> appIds = this.printerFactory.getAppIds();

        setContentType(listAppsResponse);
        try (PrintWriter writer = listAppsResponse.getWriter()) {
            JSONWriter json = new JSONWriter(writer);
            try {
                json.array();
                for (String appId: appIds) {
                    json.value(appId);
                }
                json.endArray();
            } catch (JSONException e) {
                throw new ServletException(e);
            }
        }
    }
}
