class doDSGet {
protected void doDSGet(Context context, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException,
            SQLException, AuthorizeException
    {

        String ID = "";
        String filter = "";
        String callerUrl = request.getParameter("callerUrl");

        // callerUrl must starts with URL outside DSpace request context path
        if(!callerUrl.startsWith(request.getContextPath())) {
            log.error("Controlled vocabulary caller URL would result in redirect outside DSpace web app: " + callerUrl + ". Rejecting request with 400 Bad Request.");
            response.sendError(400, "The caller URL must be within the DSpace base URL of " + request.getContextPath());
            return;
        }

        if (request.getParameter("ID") != null)
        {
            ID = request.getParameter("ID");
        }

        if (request.getParameter("filter") != null)
        {
            filter = request.getParameter("filter");
        }

        request.getSession()
                .setAttribute("controlledvocabulary.filter", filter);
        request.getSession().setAttribute("controlledvocabulary.ID", ID);
        response.sendRedirect(callerUrl);
    }
}
