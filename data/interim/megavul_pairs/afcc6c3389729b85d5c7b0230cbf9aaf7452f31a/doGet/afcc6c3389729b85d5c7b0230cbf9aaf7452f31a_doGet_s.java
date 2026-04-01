class doGet {
protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
        // Get the exception that occurred, if any
        Throwable t = (Throwable) request
                .getAttribute("javax.servlet.error.exception");

        String logInfo = UIUtil.getRequestLogInfo(request);

        // Log the error. Since we don't have a context, we need to
        // build the info "by hand"
        String logMessage = ":session_id=" + request.getSession().getId()
                + ":internal_error:" + logInfo;

        log.warn(logMessage, t);

        // Now we try and mail the designated user, if any
        UIUtil.sendAlert(request, (Exception) t);

        JSPManager.showJSP(request, response, "/error/internal.jsp");
    }
}
