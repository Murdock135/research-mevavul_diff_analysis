class doDSGet {
protected void doDSGet(Context context, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException,
            SQLException, AuthorizeException
    {

        String ID = "";
        String filter = "";
        String callerUrl = request.getParameter("callerUrl");

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
